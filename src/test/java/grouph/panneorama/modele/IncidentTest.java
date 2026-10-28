package grouph.panneorama.modele;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class IncidentTest {
    private static final ZoneId FUSEAU = ZoneId.of("Europe/Paris");
    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 10, 28, 12, 0);
    private static final Clock HORLOGE_FIGEE = Clock.fixed(MAINTENANT.atZone(FUSEAU).toInstant(), FUSEAU);

    @Test
    void incidentDeclareEstNonAffecteEtSansPrestataire() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT);

        assertThat(incident.getStatut()).isEqualTo(StatutIncident.NON_AFFECTE);
        assertThat(incident.getPrestataire()).isNull();
        assertThat(incident.getDateResolution()).isNull();
    }

    @Test
    void dureeOuvertureIncidentNonResoluEstCalculeeDepuisLHeureCourante() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT.minusHours(47));

        assertThat(incident.dureeOuverture(HORLOGE_FIGEE)).isEqualTo(Duration.ofHours(47));
    }

    @Test
    void dureeOuvertureIncidentResoluSArreteALaResolution() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT.minusDays(10));
        incident.setStatut(StatutIncident.RESOLU);
        incident.setDateResolution(MAINTENANT.minusDays(8));

        assertThat(incident.dureeOuverture(HORLOGE_FIGEE)).isEqualTo(Duration.ofDays(2));
    }
}
