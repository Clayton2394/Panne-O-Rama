package grouph.panneorama.service;

import grouph.panneorama.exception.EquipementIntrouvableException;
import grouph.panneorama.exception.IncidentIntrouvableException;
import grouph.panneorama.exception.IncidentResoluNonModifiableException;
import grouph.panneorama.modele.Equipement;
import grouph.panneorama.modele.Incident;
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
    private IncidentService incidentService;

    @BeforeEach
    void initialiser() {
        incidentRepository = mock(IncidentRepository.class);
        equipementService = mock(EquipementService.class);
        Clock horlogeFigee = Clock.fixed(MAINTENANT.atZone(FUSEAU).toInstant(), FUSEAU);
        incidentService = new IncidentService(incidentRepository, mock(EvaluationRepository.class),
                equipementService, horlogeFigee);
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
}
