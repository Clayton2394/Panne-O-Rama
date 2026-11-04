package grouph.panneorama.repository;

import grouph.panneorama.dto.PrestataireNote;
import grouph.panneorama.modele.EtatEquipement;
import grouph.panneorama.modele.Equipement;
import grouph.panneorama.modele.Evaluation;
import grouph.panneorama.modele.Incident;
import grouph.panneorama.modele.Prestataire;
import grouph.panneorama.modele.Station;
import grouph.panneorama.modele.TypeEquipement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

// La requête native s'exécute ici sur H2 : ce test vérifie aussi qu'elle reste du SQL standard.
@DataJpaTest
class PrestataireRepositoryTest {
    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 10, 28, 12, 0);

    @Autowired private PrestataireRepository prestataireRepository;
    @Autowired private IncidentRepository incidentRepository;
    @Autowired private EvaluationRepository evaluationRepository;
    @Autowired private EquipementRepository equipementRepository;
    @Autowired private StationRepository stationRepository;

    private Equipement equipement;

    @BeforeEach
    void initialiser() {
        Station station = new Station();
        station.setNom("Station de test");
        stationRepository.save(station);
        equipement = equipementRepository.save(new Equipement(station, "TEST-001", "Ascenseur de test",
                TypeEquipement.ASCENSEUR, EtatEquipement.EN_PANNE));
    }

    @Test
    void laNoteMoyenneEtLeNombreDEvaluationsSontCalculesParPrestataire() {
        Prestataire elevatorTech = enregistrerPrestataire("ElevatorTech");
        evaluer(elevatorTech, 5);
        evaluer(elevatorTech, 2);

        PrestataireNote note = prestataireRepository.listerAvecNoteMoyenne().get(0);

        assertThat(note.getNom()).isEqualTo("ElevatorTech");
        assertThat(note.getNombreEvaluations()).isEqualTo(2);
        assertThat(note.getNoteMoyenne()).isCloseTo(3.5, within(0.001));
        assertThat(note.estEvalue()).isTrue();
    }

    @Test
    void unPrestataireJamaisEvalueApparaitSansNote() {
        Prestataire evalue = enregistrerPrestataire("Alpha Maintenance");
        evaluer(evalue, 4);
        Prestataire nouveau = enregistrerPrestataire("Zeta Services");
        affecterSansEvaluer(nouveau);

        List<PrestataireNote> notes = prestataireRepository.listerAvecNoteMoyenne();

        assertThat(notes).extracting(PrestataireNote::getNom).containsExactly("Alpha Maintenance", "Zeta Services");
        PrestataireNote sansNote = notes.get(1);
        assertThat(sansNote.getNombreEvaluations()).isZero();
        assertThat(sansNote.getNoteMoyenne()).isNull();
        assertThat(sansNote.estEvalue()).isFalse();
    }

    private Prestataire enregistrerPrestataire(String nom) {
        Prestataire prestataire = new Prestataire();
        prestataire.setNom(nom);
        prestataire.setContact("contact@" + nom.toLowerCase().replace(' ', '-') + ".fr");
        return prestataireRepository.save(prestataire);
    }

    private void evaluer(Prestataire prestataire, int note) {
        Incident incident = new Incident(equipement, "Panne à évaluer", MAINTENANT.minusDays(2));
        incident.affecterPrestataire(prestataire);
        incident.resoudre(MAINTENANT.minusDays(1));
        incidentRepository.save(incident);
        evaluationRepository.save(new Evaluation(incident, note, null, MAINTENANT));
    }

    private void affecterSansEvaluer(Prestataire prestataire) {
        Incident incident = new Incident(equipement, "Panne en cours", MAINTENANT);
        incident.affecterPrestataire(prestataire);
        incidentRepository.save(incident);
    }
}
