package grouph.panneorama.service;

import grouph.panneorama.dto.LigneTableauDeBord;
import grouph.panneorama.dto.TableauDeBord;
import grouph.panneorama.modele.Equipement;
import grouph.panneorama.modele.Incident;
import grouph.panneorama.modele.NiveauAttention;
import grouph.panneorama.modele.Station;
import grouph.panneorama.modele.StatutIncident;
import grouph.panneorama.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableauDeBordServiceTest {
    private static final ZoneId FUSEAU = ZoneId.of("Europe/Paris");
    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 10, 28, 12, 0);
    private static final Clock HORLOGE_FIGEE = Clock.fixed(MAINTENANT.atZone(FUSEAU).toInstant(), FUSEAU);

    @Mock
    private IncidentRepository incidentRepository;

    private TableauDeBordService service;
    private final Station nord = station(1, "Gare du Nord");
    private final Station chatelet = station(2, "Châtelet");

    @BeforeEach
    void preparerService() {
        service = new TableauDeBordService(incidentRepository, HORLOGE_FIGEE);
    }

    @Test
    void lesIncidentsSontTriesDuPlusUrgentAuMoinsUrgent() {
        when(incidentRepository.findActifsAvecDetails()).thenReturn(List.of(
                incident(1, nord, 10, StatutIncident.NON_AFFECTE),
                incident(2, nord, 80, StatutIncident.NON_AFFECTE),
                incident(3, nord, 100, StatutIncident.AFFECTE),
                incident(4, nord, 50, StatutIncident.NON_AFFECTE)));

        TableauDeBord tableau = service.construire(null, null);

        assertThat(tableau.lignes()).extracting(LigneTableauDeBord::incidentId)
                .containsExactly(2, 4, 1, 3);
    }

    @Test
    void lIncidentAffecteNePorteJamaisDeCouleur() {
        when(incidentRepository.findActifsAvecDetails()).thenReturn(List.of(
                incident(1, nord, 100, StatutIncident.AFFECTE)));

        TableauDeBord tableau = service.construire(null, null);

        assertThat(tableau.lignes().get(0).niveau()).isEqualTo(NiveauAttention.PRIS_EN_CHARGE);
    }

    @Test
    void leFiltreParStationNeGardeQueLesIncidentsDeCetteStation() {
        when(incidentRepository.findActifsAvecDetails()).thenReturn(List.of(
                incident(1, nord, 10, StatutIncident.NON_AFFECTE),
                incident(2, chatelet, 10, StatutIncident.NON_AFFECTE)));

        TableauDeBord tableau = service.construire(2, null);

        assertThat(tableau.lignes()).extracting(LigneTableauDeBord::incidentId).containsExactly(2);
    }

    @Test
    void leFiltreParNiveauNeGardeQueCeNiveau() {
        when(incidentRepository.findActifsAvecDetails()).thenReturn(List.of(
                incident(1, nord, 10, StatutIncident.NON_AFFECTE),
                incident(2, nord, 80, StatutIncident.NON_AFFECTE)));

        TableauDeBord tableau = service.construire(null, NiveauAttention.ROUGE);

        assertThat(tableau.lignes()).extracting(LigneTableauDeBord::incidentId).containsExactly(2);
    }

    @Test
    void lesIndicateursPortentSurTousLesIncidentsActifsMemeFiltres() {
        when(incidentRepository.findActifsAvecDetails()).thenReturn(List.of(
                incident(1, nord, 10, StatutIncident.NON_AFFECTE),
                incident(2, nord, 50, StatutIncident.NON_AFFECTE),
                incident(3, nord, 80, StatutIncident.NON_AFFECTE),
                incident(4, nord, 100, StatutIncident.AFFECTE)));

        TableauDeBord tableau = service.construire(null, NiveauAttention.ROUGE);

        assertThat(tableau.lignes()).hasSize(1);
        assertThat(tableau.indicateurs().total()).isEqualTo(4);
        assertThat(tableau.indicateurs().rouges()).isEqualTo(1);
        assertThat(tableau.indicateurs().oranges()).isEqualTo(1);
        assertThat(tableau.indicateurs().verts()).isEqualTo(1);
        assertThat(tableau.indicateurs().prisEnCharge()).isEqualTo(1);
    }

    private Station station(int id, String nom) {
        Station station = new Station();
        station.setId(id);
        station.setNom(nom);
        return station;
    }

    private Incident incident(int id, Station station, long heuresOuverture, StatutIncident statut) {
        Equipement equipement = new Equipement();
        equipement.setId(id);
        equipement.setReference("EQ-" + id);
        equipement.setStation(station);
        Incident incident = new Incident(equipement, "Incident " + id, MAINTENANT.minusHours(heuresOuverture));
        incident.setId(id);
        incident.setStatut(statut);
        return incident;
    }
}