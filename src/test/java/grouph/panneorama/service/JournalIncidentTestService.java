package grouph.panneorama.service;

import grouph.panneorama.modele.Equipement;
import grouph.panneorama.modele.EvenementIncident;
import grouph.panneorama.modele.Incident;
import grouph.panneorama.modele.TypeEvenementIncident;
import grouph.panneorama.repository.EvenementIncidentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JournalIncidentServiceTest {
    private static final ZoneId FUSEAU = ZoneId.of("Europe/Paris");
    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 10, 30, 10, 0);
    private static final Clock HORLOGE_FIGEE = Clock.fixed(MAINTENANT.atZone(FUSEAU).toInstant(), FUSEAU);

    @Mock
    private EvenementIncidentRepository evenementRepository;
    @Mock private AuteurCourant auteurCourant;

    @Test
    void enregistrerHorodateAvecLHorlogeEtRetientLAuteur() {
        when(auteurCourant.identifiant()).thenReturn("resp1");
        JournalIncidentService service = new JournalIncidentService(evenementRepository, auteurCourant, HORLOGE_FIGEE);
        Incident incident = new Incident(new Equipement(), "Panne", MAINTENANT.minusHours(5));

        service.enregistrer(incident, TypeEvenementIncident.RESOLUTION, "Incident résolu");

        ArgumentCaptor<EvenementIncident> capteur = ArgumentCaptor.forClass(EvenementIncident.class);
        verify(evenementRepository).save(capteur.capture());
        assertThat(capteur.getValue().getAuteur()).isEqualTo("resp1");
        assertThat(capteur.getValue().getDateEvenement()).isEqualTo(MAINTENANT);
        assertThat(capteur.getValue().getType()).isEqualTo(TypeEvenementIncident.RESOLUTION);
    }
}