package grouph.panneorama.service;

import grouph.panneorama.exception.EquipementIntrouvableException;
import grouph.panneorama.exception.IncidentIntrouvableException;
import grouph.panneorama.exception.IncidentResoluNonAffectableException;
import grouph.panneorama.exception.IncidentResoluNonModifiableException;
import grouph.panneorama.exception.PrestataireIntrouvableException;
import grouph.panneorama.modele.Equipement;
import grouph.panneorama.modele.Incident;
import grouph.panneorama.modele.Prestataire;
import grouph.panneorama.modele.StatutIncident;
import grouph.panneorama.repository.EvaluationRepository;
import grouph.panneorama.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IncidentServiceTest {
    private static final ZoneId FUSEAU = ZoneId.of("Europe/Paris");
    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 10, 28, 12, 0);

    private IncidentRepository incidentRepository;
    private EquipementService equipementService;
    private PrestataireService prestataireService;
    private IncidentService incidentService;

    @BeforeEach
    void initialiser() {
        incidentRepository = mock(IncidentRepository.class);
        equipementService = mock(EquipementService.class);
        prestataireService = mock(PrestataireService.class);
        Clock horlogeFigee = Clock.fixed(MAINTENANT.atZone(FUSEAU).toInstant(), FUSEAU);
        incidentService = new IncidentService(incidentRepository, mock(EvaluationRepository.class),
                equipementService, prestataireService, horlogeFigee);
        when(incidentRepository.save(any(Incident.class))).thenAnswer(appel -> appel.getArgument(0));
    }

    @Test
    void declarerIncidentLeRattacheALEquipementAvecLaDateCourante() {
        Equipement equipement = new Equipement();
        when(equipementService.trouverEquipement(1)).thenReturn(equipement);

        Incident incident = incidentService.declarerIncident(1, "Porte bloquée au quai 1");

        assertThat(incident.getEquipement()).isSameAs(equipement);
        assertThat(incident.getDateOuverture()).isEqualTo(MAINTENANT);
        assertThat(incident.getStatut()).isEqualTo(StatutIncident.NON_AFFECTE);
        verify(incidentRepository).save(incident);
    }

    @Test
    void declarerIncidentSurEquipementInexistantNEnregistreRien() {
        when(equipementService.trouverEquipement(99)).thenThrow(new EquipementIntrouvableException(99));

        assertThatThrownBy(() -> incidentService.declarerIncident(99, "Porte bloquée au quai 1"))
                .isInstanceOf(EquipementIntrouvableException.class);
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void trouverIncidentInexistantLeveIncidentIntrouvable() {
        when(incidentRepository.trouverAvecEquipementEtPrestataire(42)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> incidentService.trouverIncident(42))
                .isInstanceOf(IncidentIntrouvableException.class)
                .hasMessage("L'incident 42 n'existe pas");
    }

    @Test
    void modifierDescriptionMetAJourLIncidentExistant() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT);
        when(incidentRepository.trouverAvecEquipementEtPrestataire(1)).thenReturn(Optional.of(incident));

        Incident incidentModifie = incidentService.modifierDescription(1, "Porte bloquée au quai 1, côté gauche");

        assertThat(incidentModifie.getDescription()).isEqualTo("Porte bloquée au quai 1, côté gauche");
    }

    @Test
    void trouverIncidentModifiableResoluLeveIncidentResoluNonModifiable() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT.minusDays(10));
        incident.setStatut(StatutIncident.RESOLU);
        when(incidentRepository.trouverAvecEquipementEtPrestataire(5)).thenReturn(Optional.of(incident));

        assertThatThrownBy(() -> incidentService.trouverIncidentModifiable(5))
                .isInstanceOf(IncidentResoluNonModifiableException.class);
    }

    @Test
    void affecterPrestataireRattacheLePrestataireALIncident() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT);
        Prestataire prestataire = new Prestataire();
        when(incidentRepository.trouverAvecEquipementEtPrestataire(1)).thenReturn(Optional.of(incident));
        when(prestataireService.trouverPrestataire(2)).thenReturn(prestataire);

        Incident incidentAffecte = incidentService.affecterPrestataire(1, 2);

        assertThat(incidentAffecte.getPrestataire()).isSameAs(prestataire);
        assertThat(incidentAffecte.getStatut()).isEqualTo(StatutIncident.AFFECTE);
    }

    @Test
    void affecterPrestataireInexistantLevePrestataireIntrouvableEtNeChangeRien() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT);
        when(incidentRepository.trouverAvecEquipementEtPrestataire(1)).thenReturn(Optional.of(incident));
        when(prestataireService.trouverPrestataire(99)).thenThrow(new PrestataireIntrouvableException(99));

        assertThatThrownBy(() -> incidentService.affecterPrestataire(1, 99))
                .isInstanceOf(PrestataireIntrouvableException.class);
        assertThat(incident.getPrestataire()).isNull();
        assertThat(incident.getStatut()).isEqualTo(StatutIncident.NON_AFFECTE);
    }

    @Test
    void trouverIncidentAffectableResoluLeveIncidentResoluNonAffectable() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT.minusDays(10));
        incident.setStatut(StatutIncident.RESOLU);
        when(incidentRepository.trouverAvecEquipementEtPrestataire(5)).thenReturn(Optional.of(incident));

        assertThatThrownBy(() -> incidentService.trouverIncidentAffectable(5))
                .isInstanceOf(IncidentResoluNonAffectableException.class);
    }
}
