package grouph.panneorama.service;

import grouph.panneorama.modele.Equipement;
import grouph.panneorama.modele.EtatEquipement;
import grouph.panneorama.modele.Incident;
import grouph.panneorama.modele.Prestataire;
import grouph.panneorama.modele.Station;
import grouph.panneorama.modele.TypeEquipement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExportHistoriqueServiceTest {
    private static final ZoneId FUSEAU = ZoneId.of("Europe/Paris");
    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 10, 28, 12, 0);

    private IncidentService incidentService;
    private ExportHistoriqueService exportHistoriqueService;

    @BeforeEach
    void initialiser() {
        incidentService = mock(IncidentService.class);
        Clock horlogeFigee = Clock.fixed(MAINTENANT.atZone(FUSEAU).toInstant(), FUSEAU);
        exportHistoriqueService = new ExportHistoriqueService(incidentService, horlogeFigee);
    }

    @Test
    void lExportCommenceParLaMarqueUtf8EtLEnTete() {
        when(incidentService.listerHistoriqueResolus()).thenReturn(List.of());

        String csv = exportHistoriqueService.exporterHistoriqueCsv();

        assertThat(csv).startsWith("﻿Numéro;Station;Référence équipement;Type équipement;"
                + "Description;Ouvert le;Résolu le;Durée (heures);Prestataire");
    }

    @Test
    void chaqueIncidentResoluDonneUneLigneAvecSaDureeEnHeures() {
        when(incidentService.listerHistoriqueResolus()).thenReturn(List.of(incidentResolu("Porte bloquée")));

        String[] lignes = exportHistoriqueService.exporterHistoriqueCsv().split("\r\n");

        assertThat(lignes).hasSize(2);
        assertThat(lignes[1]).isEqualTo(";Gare du Nord;ASC-001;ASCENSEUR;Porte bloquée;"
                + "26/10/2026 12:00;27/10/2026 18:00;30;ElevatorTech");
    }

    @Test
    void uneDescriptionContenantLeSeparateurEstEntreGuillemets() {
        when(incidentService.listerHistoriqueResolus()).thenReturn(List.of(incidentResolu("Porte; bloquée")));

        String csv = exportHistoriqueService.exporterHistoriqueCsv();

        assertThat(csv).contains(";\"Porte; bloquée\";");
    }

    private Incident incidentResolu(String description) {
        Station station = new Station();
        station.setNom("Gare du Nord");
        Equipement equipement = new Equipement(station, "ASC-001", "Ascenseur quai 1",
                TypeEquipement.ASCENSEUR, EtatEquipement.EN_PANNE);
        Prestataire prestataire = new Prestataire();
        prestataire.setNom("ElevatorTech");
        Incident incident = new Incident(equipement, description, MAINTENANT.minusDays(2));
        incident.affecterPrestataire(prestataire);
        incident.resoudre(MAINTENANT.minusDays(2).plusHours(30));
        return incident;
    }
}
