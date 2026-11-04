package grouph.panneorama.modele;

import grouph.panneorama.exception.IncidentNonEvaluableException;
import grouph.panneorama.exception.NoteEvaluationInvalideException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluationTest {
    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 10, 28, 12, 0);

    @Test
    void evaluerUnIncidentResoluEtAffecteEnregistreLaNoteEtLaDate() {
        Incident incident = incidentResoluEtAffecte();

        Evaluation evaluation = new Evaluation(incident, 4, "Intervention rapide", MAINTENANT);

        assertThat(evaluation.getIncident()).isSameAs(incident);
        assertThat(evaluation.getNote()).isEqualTo(4);
        assertThat(evaluation.getCommentaire()).isEqualTo("Intervention rapide");
        assertThat(evaluation.getDateEvaluation()).isEqualTo(MAINTENANT);
    }

    @Test
    void unCommentaireVideNEstPasConserve() {
        Evaluation evaluation = new Evaluation(incidentResoluEtAffecte(), 3, "   ", MAINTENANT);

        assertThat(evaluation.getCommentaire()).isNull();
    }

    @Test
    void lesNotesAuxBornesSontAcceptees() {
        assertThat(new Evaluation(incidentResoluEtAffecte(), Evaluation.NOTE_MINIMALE, null, MAINTENANT).getNote())
                .isEqualTo(Evaluation.NOTE_MINIMALE);
        assertThat(new Evaluation(incidentResoluEtAffecte(), Evaluation.NOTE_MAXIMALE, null, MAINTENANT).getNote())
                .isEqualTo(Evaluation.NOTE_MAXIMALE);
    }

    @Test
    void uneNoteHorsBornesEstRefusee() {
        assertThatThrownBy(() -> new Evaluation(incidentResoluEtAffecte(), Evaluation.NOTE_MAXIMALE + 1, null, MAINTENANT))
                .isInstanceOf(NoteEvaluationInvalideException.class);
        assertThatThrownBy(() -> new Evaluation(incidentResoluEtAffecte(), Evaluation.NOTE_MINIMALE - 1, null, MAINTENANT))
                .isInstanceOf(NoteEvaluationInvalideException.class);
    }

    @Test
    void evaluerUnIncidentNonResoluEstInterdit() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT);
        incident.affecterPrestataire(new Prestataire());

        assertThatThrownBy(() -> new Evaluation(incident, 4, null, MAINTENANT))
                .isInstanceOf(IncidentNonEvaluableException.class);
    }

    private Incident incidentResoluEtAffecte() {
        Incident incident = new Incident(new Equipement(), "Porte bloquée au quai 1", MAINTENANT.minusDays(1));
        incident.affecterPrestataire(new Prestataire());
        incident.resoudre(MAINTENANT.minusHours(1));
        return incident;
    }
}
