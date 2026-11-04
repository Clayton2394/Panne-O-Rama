package grouph.panneorama.service;

import grouph.panneorama.dto.RapportImport;
import grouph.panneorama.exception.StationIntrouvableException;
import grouph.panneorama.modele.Equipement;
import grouph.panneorama.modele.EtatEquipement;
import grouph.panneorama.modele.Station;
import grouph.panneorama.modele.TypeEquipement;
import grouph.panneorama.repository.EquipementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.StringReader;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImportEquipementsServiceTest {
    private static final String EN_TETE = "reference;description;type;etat\n";

    private EquipementRepository equipementRepository;
    private StationService stationService;
    private ImportEquipementsService importEquipementsService;
    private Station station;

    @BeforeEach
    void initialiser() {
        equipementRepository = mock(EquipementRepository.class);
        stationService = mock(StationService.class);
        importEquipementsService = new ImportEquipementsService(equipementRepository, stationService);
        station = new Station();
        when(stationService.trouverStation(1)).thenReturn(station);
        when(equipementRepository.trouverReferencesExistantes(anyCollection())).thenReturn(Set.of());
    }

    @Test
    void importerUnFichierValideEnregistreTousLesEquipementsDansLaStation() {
        RapportImport rapport = importer(EN_TETE
                + "ASC-101;Ascenseur quai 2;ASCENSEUR;EN_PANNE\n"
                + "ECR-102;Écran hall nord;ecran_information;\n");

        assertThat(rapport.estReussi()).isTrue();
        assertThat(rapport.nombreImportes()).isEqualTo(2);
        List<Equipement> enregistres = equipementsEnregistres();
        assertThat(enregistres).extracting(Equipement::getReference).containsExactly("ASC-101", "ECR-102");
        assertThat(enregistres).allSatisfy(equipement -> assertThat(equipement.getStation()).isSameAs(station));
        assertThat(enregistres.get(0).getEtatFonctionnement()).isEqualTo(EtatEquipement.EN_PANNE);
        assertThat(enregistres.get(1).getType()).isEqualTo(TypeEquipement.ECRAN_INFORMATION);
    }

    @Test
    void unEtatVideDonneUnEquipementFonctionnel() {
        importer(EN_TETE + "POR-103;;PORTIQUE;\n");

        Equipement equipement = equipementsEnregistres().get(0);
        assertThat(equipement.getEtatFonctionnement()).isEqualTo(EtatEquipement.FONCTIONNEL);
        assertThat(equipement.getDescription()).isNull();
    }

    @Test
    void laMarqueUtf8DExcelEstIgnoree() {
        RapportImport rapport = importer("﻿" + EN_TETE + "ASC-101;Ascenseur;ASCENSEUR;\n");

        assertThat(rapport.estReussi()).isTrue();
    }

    @Test
    void uneSeuleLigneInvalideRefuseToutLImport() {
        RapportImport rapport = importer(EN_TETE
                + "ASC-101;Ascenseur quai 2;ASCENSEUR;FONCTIONNEL\n"
                + "XXX-102;Inconnu;FUSEE;FONCTIONNEL\n");

        assertThat(rapport.estReussi()).isFalse();
        assertThat(rapport.nombreImportes()).isZero();
        assertThat(rapport.erreurs()).singleElement().asString()
                .startsWith("Ligne 3 : type « FUSEE » inconnu");
        verify(equipementRepository, never()).saveAll(any());
    }

    @Test
    void touteslesErreursSontSignaleesAvecLeurNumeroDeLigne() {
        RapportImport rapport = importer(EN_TETE
                + ";Sans référence;ASCENSEUR;\n"
                + "ASC-102;Sans type;;\n"
                + "ASC-103;État faux;ASCENSEUR;CASSE\n");

        assertThat(rapport.erreurs()).containsExactly(
                "Ligne 2 : la référence est obligatoire",
                "Ligne 3 : type obligatoire",
                "Ligne 4 : état « CASSE » inconnu (valeurs possibles : [FONCTIONNEL, EN_PANNE])");
    }

    @Test
    void uneReferenceEnDoubleDansLeFichierEstRefusee() {
        RapportImport rapport = importer(EN_TETE
                + "ASC-101;Premier;ASCENSEUR;\n"
                + "ASC-101;Doublon;ASCENSEUR;\n");

        assertThat(rapport.erreurs()).containsExactly(
                "Ligne 3 : la référence « ASC-101 » apparaît plusieurs fois dans le fichier");
    }

    @Test
    void uneReferenceDejaEnBaseEstRefuseeSelonRG9() {
        when(equipementRepository.trouverReferencesExistantes(anyCollection())).thenReturn(Set.of("ASC-001"));

        RapportImport rapport = importer(EN_TETE + "ASC-001;Déjà là;ASCENSEUR;\n");

        assertThat(rapport.erreurs()).containsExactly("La référence « ASC-001 » existe déjà");
        verify(equipementRepository, never()).saveAll(any());
    }

    @Test
    void uneColonneObligatoireManquanteEstSignalee() {
        RapportImport rapport = importer("reference;description\nASC-101;Ascenseur\n");

        assertThat(rapport.erreurs()).containsExactly("Colonne obligatoire manquante : « type »");
    }

    @Test
    void unFichierSansEquipementEstRefuse() {
        RapportImport rapport = importer(EN_TETE);

        assertThat(rapport.erreurs()).containsExactly("Le fichier ne contient aucun équipement");
    }

    @Test
    void importerDansUneStationInexistanteLeveStationIntrouvable() {
        when(stationService.trouverStation(99)).thenThrow(new StationIntrouvableException(99));

        assertThatThrownBy(() -> importEquipementsService.importerEquipements(99, new StringReader(EN_TETE)))
                .isInstanceOf(StationIntrouvableException.class);
    }

    private RapportImport importer(String contenu) {
        return importEquipementsService.importerEquipements(1, new StringReader(contenu));
    }

    @SuppressWarnings("unchecked")
    private List<Equipement> equipementsEnregistres() {
        ArgumentCaptor<List<Equipement>> capteur = ArgumentCaptor.forClass(List.class);
        verify(equipementRepository).saveAll(capteur.capture());
        return capteur.getValue();
    }
}
