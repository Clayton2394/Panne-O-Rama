package grouph.panneorama.repository;

import grouph.panneorama.modele.EtatEquipement;
import grouph.panneorama.modele.Equipement;
import grouph.panneorama.modele.Evaluation;
import grouph.panneorama.modele.Incident;
import grouph.panneorama.modele.Prestataire;
import grouph.panneorama.modele.Station;
import grouph.panneorama.modele.SurveillancePrestataire;
import grouph.panneorama.modele.TypeEquipement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Limit;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// PS1 : « les 5 dernières évaluations » d'un prestataire, vérifiées sur la vraie requête (H2).
@DataJpaTest
class EvaluationRepositoryTest {
    private static final LocalDateTime DEBUT = LocalDateTime.of(2026, 10, 1, 9, 0);
    private static final Limit FENETRE = Limit.of(SurveillancePrestataire.TAILLE_FENETRE);

    @Autowired private EvaluationRepository evaluationRepository;
    @Autowired private PrestataireRepository prestataireRepository;
    @Autowired private IncidentRepository incidentRepository;
    @Autowired private EquipementRepository equipementRepository;
    @Autowired private StationRepository stationRepository;

    private Equipement equipement;
    private Prestataire prestataire;

    @BeforeEach
    void initialiser() {
        Station station = new Station();
        station.setNom("Station de test");
        stationRepository.save(station);
        equipement = equipementRepository.save(new Equipement(station, "TEST-001", "Ascenseur de test",
                TypeEquipement.ASCENSEUR, EtatEquipement.EN_PANNE));
        prestataire = enregistrerPrestataire("ElevatorTech");
    }

    @Test
    void seulesLesCinqPlusRecentesSontRetenuesDeLaPlusRecenteALaPlusAncienne() {
        // Exemple 4 de l'énoncé, de la plus ancienne à la plus récente : 3, 3, 3, 3, 3, 2, 5.
        int[] notes = {3, 3, 3, 3, 3, 2, 5};
        for (int jour = 0; jour < notes.length; jour++) {
            evaluer(prestataire, notes[jour], DEBUT.plusDays(jour));
        }

        List<Evaluation> dernieres = evaluationRepository.listerPlusRecentesDuPrestataire(prestataire.getId(), FENETRE);

        assertThat(dernieres).extracting(Evaluation::getNote).containsExactly(5, 2, 3, 3, 3);
        assertThat(dernieres).extracting(Evaluation::getDateEvaluation)
                .containsExactly(DEBUT.plusDays(6), DEBUT.plusDays(5), DEBUT.plusDays(4),
                        DEBUT.plusDays(3), DEBUT.plusDays(2));
    }

    @Test
    void aDateEgaleLIdentifiantLePlusGrandPasseEnPremier() {
        Evaluation premiere = evaluer(prestataire, 1, DEBUT);
        Evaluation seconde = evaluer(prestataire, 4, DEBUT);

        List<Evaluation> dernieres = evaluationRepository.listerPlusRecentesDuPrestataire(prestataire.getId(), FENETRE);

        assertThat(dernieres).extracting(Evaluation::getId).containsExactly(seconde.getId(), premiere.getId());
    }

    @Test
    void lesEvaluationsDesAutresPrestatairesSontIgnorees() {
        Prestataire concurrent = enregistrerPrestataire("Zeta Services");
        evaluer(prestataire, 2, DEBUT);
        evaluer(concurrent, 5, DEBUT.plusDays(1));

        List<Evaluation> dernieres = evaluationRepository.listerPlusRecentesDuPrestataire(prestataire.getId(), FENETRE);

        assertThat(dernieres).extracting(Evaluation::getNote).containsExactly(2);
    }

    @Test
    void moinsDeCinqEvaluationsSontToutesRenvoyees() {
        // Exemple 1 de l'énoncé : 4, 1, 1, 1.
        int[] notes = {4, 1, 1, 1};
        for (int jour = 0; jour < notes.length; jour++) {
            evaluer(prestataire, notes[jour], DEBUT.plusDays(jour));
        }

        List<Evaluation> dernieres = evaluationRepository.listerPlusRecentesDuPrestataire(prestataire.getId(), FENETRE);

        assertThat(dernieres).extracting(Evaluation::getNote).containsExactly(1, 1, 1, 4);
    }

    private Prestataire enregistrerPrestataire(String nom) {
        Prestataire nouveau = new Prestataire();
        nouveau.setNom(nom);
        nouveau.setContact("contact@" + nom.toLowerCase().replace(' ', '-') + ".fr");
        return prestataireRepository.save(nouveau);
    }

    private Evaluation evaluer(Prestataire evalue, int note, LocalDateTime dateEvaluation) {
        Incident incident = new Incident(equipement, "Panne à évaluer", dateEvaluation.minusDays(2));
        incident.affecterPrestataire(evalue);
        incident.resoudre(dateEvaluation.minusDays(1));
        incidentRepository.save(incident);
        return evaluationRepository.save(new Evaluation(incident, note, null, dateEvaluation));
    }
}
