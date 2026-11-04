package grouph.panneorama.modele;

import grouph.panneorama.exception.IncidentNonEvaluableException;
import grouph.panneorama.exception.IncidentResoluNonAffectableException;
import grouph.panneorama.exception.IncidentResoluNonModifiableException;
import grouph.panneorama.exception.TransitionIncidentInterditeException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        incident.resoudre(MAINTENANT.minusDays(8));

        assertThat(incident.dureeOuverture(HORLOGE_FIGEE)).isEqualTo(Duration.ofDays(2));
    }

    @Test
    void resoudreUnIncidentOuvertEnregistreLaDateEtLePasseAResolu() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT.minusHours(5));

        incident.resoudre(MAINTENANT);

        assertThat(incident.estResolu()).isTrue();
        assertThat(incident.getStatut()).isEqualTo(StatutIncident.RESOLU);
        assertThat(incident.getDateResolution()).isEqualTo(MAINTENANT);
    }

    @Test
    void resoudreUnIncidentDejaResoluEstInterdit() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT.minusHours(5));
        incident.resoudre(MAINTENANT.minusHours(1));

        assertThatThrownBy(() -> incident.resoudre(MAINTENANT))
                .isInstanceOf(TransitionIncidentInterditeException.class)
                .hasMessageContaining("déjà résolu");
    }

    @Test
    void resoudreAvantLOuvertureEstInterdit() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT);

        assertThatThrownBy(() -> incident.resoudre(MAINTENANT.minusMinutes(1)))
                .isInstanceOf(TransitionIncidentInterditeException.class)
                .hasMessageContaining("ne peut pas précéder");
    }

    @Test
    void resoudreExactementALOuvertureEstAutorise() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT);

        incident.resoudre(MAINTENANT);

        assertThat(incident.estResolu()).isTrue();
    }

    @Test
    void uneResolutionRefuseeLaisseLIncidentInchange() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT);

        assertThatThrownBy(() -> incident.resoudre(MAINTENANT.minusMinutes(1)))
                .isInstanceOf(TransitionIncidentInterditeException.class);

        assertThat(incident.getStatut()).isEqualTo(StatutIncident.NON_AFFECTE);
        assertThat(incident.getDateResolution()).isNull();
    }

    @Test
    void modifierDescriptionIncidentNonResoluRemplaceLaDescription() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT);

        incident.modifierDescription("Porte bloquée au quai 1, côté gauche");

        assertThat(incident.getDescription()).isEqualTo("Porte bloquée au quai 1, côté gauche");
    }

    @Test
    void modifierDescriptionIncidentResoluEstInterditEtNeChangeRien() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT.minusDays(10));
        incident.resoudre(MAINTENANT);

        assertThatThrownBy(() -> incident.modifierDescription("Nouvelle description"))
                .isInstanceOf(IncidentResoluNonModifiableException.class);
        assertThat(incident.getDescription()).isEqualTo("Porte bloquée au quai 1");
    }

    @Test
    void affecterPrestataireIncidentNonAffecteLePasseEnAffecte() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT);
        Prestataire prestataire = new Prestataire();

        incident.affecterPrestataire(prestataire);

        assertThat(incident.getPrestataire()).isSameAs(prestataire);
        assertThat(incident.getStatut()).isEqualTo(StatutIncident.AFFECTE);
    }

    @Test
    void reaffecterPrestataireRemplaceLePrestataireActuel() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT);
        Prestataire premierPrestataire = new Prestataire();
        Prestataire nouveauPrestataire = new Prestataire();
        incident.affecterPrestataire(premierPrestataire);

        incident.affecterPrestataire(nouveauPrestataire);

        assertThat(incident.getPrestataire()).isSameAs(nouveauPrestataire);
        assertThat(incident.getStatut()).isEqualTo(StatutIncident.AFFECTE);
    }

    @Test
    void affecterPrestataireIncidentResoluEstInterditEtNeChangeRien() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT.minusDays(10));
        Prestataire prestataireInitial = new Prestataire();
        incident.affecterPrestataire(prestataireInitial);
        incident.resoudre(MAINTENANT);

        assertThatThrownBy(() -> incident.affecterPrestataire(new Prestataire()))
                .isInstanceOf(IncidentResoluNonAffectableException.class);
        assertThat(incident.getPrestataire()).isSameAs(prestataireInitial);
        assertThat(incident.getStatut()).isEqualTo(StatutIncident.RESOLU);
    }

    @Test
    void incidentResoluAvecPrestataireEstEvaluable() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT.minusDays(1));
        incident.affecterPrestataire(new Prestataire());
        incident.resoudre(MAINTENANT);

        assertThat(incident.estEvaluable()).isTrue();
    }

    @Test
    void incidentNonResoluNEstPasEvaluable() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT);
        incident.affecterPrestataire(new Prestataire());

        assertThat(incident.estEvaluable()).isFalse();
        assertThatThrownBy(incident::verifierEvaluable).isInstanceOf(IncidentNonEvaluableException.class);
    }

    @Test
    void incidentResoluSansPrestataireNEstPasEvaluable() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT.minusDays(1));
        incident.resoudre(MAINTENANT);

        assertThat(incident.estEvaluable()).isFalse();
        assertThatThrownBy(incident::verifierEvaluable).isInstanceOf(IncidentNonEvaluableException.class);
    }

    @Test
    void incidentNonAffecteOuvertDepuis47HeuresEstVert() {
        Incident incident = new Incident(new Equipement(), "Panne", MAINTENANT.minusHours(47));

        assertThat(incident.niveauAttention(HORLOGE_FIGEE)).isEqualTo(NiveauAttention.VERT);
    }

    @Test
    void incidentNonAffecteOuvertDepuis48HeuresPileEstOrange() {
        Incident incident = new Incident(new Equipement(), "Panne", MAINTENANT.minusHours(48));

        assertThat(incident.niveauAttention(HORLOGE_FIGEE)).isEqualTo(NiveauAttention.ORANGE);
    }

    @Test
    void incidentNonAffecteOuvertDepuis71Heures59EstOrange() {
        Incident incident = new Incident(new Equipement(), "Panne", MAINTENANT.minusHours(72).plusMinutes(1));

        assertThat(incident.niveauAttention(HORLOGE_FIGEE)).isEqualTo(NiveauAttention.ORANGE);
    }

    @Test
    void incidentNonAffecteOuvertDepuis72HeuresPileEstRouge() {
        Incident incident = new Incident(new Equipement(), "Panne", MAINTENANT.minusHours(72));

        assertThat(incident.niveauAttention(HORLOGE_FIGEE)).isEqualTo(NiveauAttention.ROUGE);
    }

    @Test
    void incidentAffecteEstPrisEnChargeMemeOuvertDepuisTresLongtemps() {
        Incident incident = new Incident(new Equipement(), "Panne", MAINTENANT.minusHours(100));
        incident.setStatut(StatutIncident.AFFECTE);

        assertThat(incident.niveauAttention(HORLOGE_FIGEE)).isEqualTo(NiveauAttention.PRIS_EN_CHARGE);
    }

    @Test
    void incidentResoluNAPasDeNiveauDAttention() {
        Incident incident = new Incident(new Equipement(), "Panne", MAINTENANT.minusHours(10));
        incident.setStatut(StatutIncident.RESOLU);
        incident.setDateResolution(MAINTENANT);

        assertThatThrownBy(() -> incident.niveauAttention(HORLOGE_FIGEE))
                .isInstanceOf(IllegalStateException.class);
    }
}
