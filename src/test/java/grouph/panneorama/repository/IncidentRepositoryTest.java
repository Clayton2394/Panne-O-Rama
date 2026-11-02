package grouph.panneorama.repository;

import grouph.panneorama.modele.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class IncidentRepositoryTest {

    @Autowired
    private IncidentRepository incidentRepository;
    @Autowired private EquipementRepository equipementRepository;
    @Autowired private StationRepository stationRepository;

    @Test
    void findHistoriqueResolus_trieDuPlusRecentAuPlusAncien() {
        Equipement equipement = creerEquipement();
        LocalDateTime maintenant = LocalDateTime.now();
        enregistrerResolu(equipement, "ancien", maintenant.minusDays(8));
        enregistrerResolu(equipement, "récent", maintenant.minusDays(1));
        enregistrerResolu(equipement, "milieu", maintenant.minusDays(3));

        List<Incident> historique = incidentRepository.findHistoriqueResolus();

        assertEquals(List.of("récent", "milieu", "ancien"),
                historique.stream().map(Incident::getDescription).toList());
    }

    @Test
    void findHistoriqueResolus_exclutLesIncidentsNonResolus() {
        Equipement equipement = creerEquipement();
        incidentRepository.save(new Incident(equipement, "en cours", LocalDateTime.now().minusHours(47)));

        assertTrue(incidentRepository.findHistoriqueResolus().isEmpty());
    }

    private void enregistrerResolu(Equipement equipement, String description, LocalDateTime resolution) {
        Incident incident = new Incident(equipement, description, resolution.minusDays(1));
        incident.setStatut(StatutIncident.RESOLU);
        incident.setDateResolution(resolution);
        incidentRepository.save(incident);
    }

    private Equipement creerEquipement() {
        Station station = new Station();
        station.setNom("Station de test");
        stationRepository.save(station);

        Equipement equipement = new Equipement();
        equipement.setReference("TEST-001");
        equipement.setDescription("Ascenseur de test");
        equipement.setType(TypeEquipement.ASCENSEUR);
        equipement.setEtatFonctionnement(EtatEquipement.EN_PANNE);
        equipement.setStation(station);
        return equipementRepository.save(equipement);
    }
}
