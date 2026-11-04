package grouph.panneorama.service;

import grouph.panneorama.dto.RapportImport;
import grouph.panneorama.modele.Equipement;
import grouph.panneorama.modele.EtatEquipement;
import grouph.panneorama.modele.Station;
import grouph.panneorama.modele.TypeEquipement;
import grouph.panneorama.repository.EquipementRepository;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class ImportEquipementsService {
    private static final Logger log = LoggerFactory.getLogger(ImportEquipementsService.class);

    static final String COLONNE_REFERENCE = "reference";
    static final String COLONNE_DESCRIPTION = "description";
    static final String COLONNE_TYPE = "type";
    static final String COLONNE_ETAT = "etat";
    private static final List<String> COLONNES_OBLIGATOIRES = List.of(COLONNE_REFERENCE, COLONNE_TYPE);
    // La ligne 1 du fichier est l'en-tête : le premier équipement est donc en ligne 2.
    private static final int DECALAGE_EN_TETE = 1;

    private final EquipementRepository equipementRepository;
    private final StationService stationService;

    public ImportEquipementsService(EquipementRepository equipementRepository, StationService stationService) {
        this.equipementRepository = equipementRepository;
        this.stationService = stationService;
    }

    // Lot alpha 9 : tout ou rien, on valide toutes les lignes avant d'enregistrer quoi que ce soit (RG10).
    @Transactional
    @PreAuthorize("hasRole('RESPONSABLE')")
    public RapportImport importerEquipements(Integer stationId, Reader contenu) {
        Station station = stationService.trouverStation(stationId);
        List<String> erreurs = new ArrayList<>();
        List<Equipement> equipements = new ArrayList<>();

        try (CSVParser lecteur = FormatCsv.LECTURE.parse(sansMarqueUtf8(contenu))) {
            if (!verifierColonnes(lecteur, erreurs)) {
                return RapportImport.refuse(erreurs);
            }
            Set<String> referencesDuFichier = new HashSet<>();
            for (CSVRecord enregistrement : lecteur) {
                lireLigne(enregistrement, station, referencesDuFichier, erreurs)
                        .ifPresent(equipements::add);
            }
        } catch (IOException | UncheckedIOException | IllegalArgumentException | IllegalStateException exception) {
            log.warn("Import CSV illisible pour la station {} : {}", stationId, exception.getMessage());
            return RapportImport.refuse(List.of("Le fichier n'est pas un CSV valide (séparateur attendu : « "
                    + FormatCsv.SEPARATEUR + " »)"));
        }

        if (equipements.isEmpty() && erreurs.isEmpty()) {
            erreurs.add("Le fichier ne contient aucun équipement");
        }
        verifierReferencesDejaEnBase(equipements, erreurs);
        if (!erreurs.isEmpty()) {
            return RapportImport.refuse(erreurs);
        }
        equipementRepository.saveAll(equipements);
        log.info("{} équipement(s) importé(s) dans la station {}", equipements.size(), stationId);
        return RapportImport.reussi(equipements.size());
    }

    private boolean verifierColonnes(CSVParser lecteur, List<String> erreurs) {
        Set<String> colonnes = new HashSet<>();
        lecteur.getHeaderNames().forEach(colonne -> colonnes.add(colonne.toLowerCase(Locale.ROOT)));
        for (String colonneObligatoire : COLONNES_OBLIGATOIRES) {
            if (!colonnes.contains(colonneObligatoire)) {
                erreurs.add("Colonne obligatoire manquante : « " + colonneObligatoire + " »");
            }
        }
        return erreurs.isEmpty();
    }

    private Optional<Equipement> lireLigne(CSVRecord enregistrement, Station station,
                                           Set<String> referencesDuFichier, List<String> erreurs) {
        String prefixe = "Ligne " + (enregistrement.getRecordNumber() + DECALAGE_EN_TETE) + " : ";
        int nombreErreursAvant = erreurs.size();

        String reference = valeur(enregistrement, COLONNE_REFERENCE);
        if (reference.isEmpty()) {
            erreurs.add(prefixe + "la référence est obligatoire");
        } else if (reference.length() > Equipement.LONGUEUR_MAX_REFERENCE) {
            erreurs.add(prefixe + "la référence dépasse " + Equipement.LONGUEUR_MAX_REFERENCE + " caractères");
        } else if (!referencesDuFichier.add(reference)) {
            erreurs.add(prefixe + "la référence « " + reference + " » apparaît plusieurs fois dans le fichier");
        }

        String description = valeur(enregistrement, COLONNE_DESCRIPTION);
        if (description.length() > Equipement.LONGUEUR_MAX_DESCRIPTION) {
            erreurs.add(prefixe + "la description dépasse " + Equipement.LONGUEUR_MAX_DESCRIPTION + " caractères");
        }

        TypeEquipement type = lireEnumeration(TypeEquipement.class, valeur(enregistrement, COLONNE_TYPE),
                null, prefixe + "type", erreurs);
        // Un équipement importé sans état est considéré comme fonctionnel.
        EtatEquipement etat = lireEnumeration(EtatEquipement.class, valeur(enregistrement, COLONNE_ETAT),
                EtatEquipement.FONCTIONNEL, prefixe + "état", erreurs);

        if (erreurs.size() > nombreErreursAvant) {
            return Optional.empty();
        }
        return Optional.of(new Equipement(station, reference,
                description.isEmpty() ? null : description, type, etat));
    }

    private <E extends Enum<E>> E lireEnumeration(Class<E> enumeration, String texte, E valeurParDefaut,
                                                  String libelle, List<String> erreurs) {
        if (texte.isEmpty()) {
            if (valeurParDefaut == null) {
                erreurs.add(libelle + " obligatoire");
            }
            return valeurParDefaut;
        }
        try {
            return Enum.valueOf(enumeration, texte.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            erreurs.add(libelle + " « " + texte + " » inconnu (valeurs possibles : "
                    + Arrays.toString(enumeration.getEnumConstants()) + ")");
            return null;
        }
    }

    private void verifierReferencesDejaEnBase(List<Equipement> equipements, List<String> erreurs) {
        if (equipements.isEmpty()) {
            return;
        }
        List<String> references = equipements.stream().map(Equipement::getReference).toList();
        // RG9 : la référence d'un équipement est unique dans tout le réseau.
        equipementRepository.trouverReferencesExistantes(references).stream().sorted()
                .forEach(reference -> erreurs.add("La référence « " + reference + " » existe déjà"));
    }

    private static String valeur(CSVRecord enregistrement, String colonne) {
        if (!enregistrement.isMapped(colonne) || !enregistrement.isSet(colonne)) {
            return "";
        }
        return enregistrement.get(colonne).strip();
    }

    // Excel ajoute souvent une marque UTF-8 invisible devant le premier en-tête : on la retire.
    private static Reader sansMarqueUtf8(Reader contenu) throws IOException {
        PushbackReader lecteur = new PushbackReader(contenu);
        int premierCaractere = lecteur.read();
        if (premierCaractere != -1 && premierCaractere != FormatCsv.MARQUE_UTF8.charAt(0)) {
            lecteur.unread(premierCaractere);
        }
        return lecteur;
    }
}
