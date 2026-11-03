package grouph.panneorama.modele;

import grouph.panneorama.exception.NoteEvaluationInvalideException;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Entity
@Table(name = "evaluation")
public class Evaluation {
    public static final int NOTE_MINIMALE = 1;
    public static final int NOTE_MAXIMALE = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "note", nullable = false)
    private Integer note;

    @Lob
    @Column(name = "commentaire")
    private String commentaire;

    @Column(name = "date_evaluation", nullable = false)
    private LocalDateTime dateEvaluation;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false, unique = true)
    private Incident incident;

    protected Evaluation() {
    }

    // RG8 : seul un incident résolu et affecté s'évalue, avec une note entière de 1 à 5 et un commentaire facultatif.
    public Evaluation(Incident incident, int note, String commentaire, LocalDateTime dateEvaluation) {
        incident.verifierEvaluable();
        if (note < NOTE_MINIMALE || note > NOTE_MAXIMALE) {
            throw new NoteEvaluationInvalideException(NOTE_MINIMALE, NOTE_MAXIMALE);
        }
        this.incident = incident;
        this.note = note;
        this.commentaire = commentaire == null || commentaire.isBlank() ? null : commentaire.strip();
        this.dateEvaluation = dateEvaluation;
    }

    public static List<Integer> notesPossibles() {
        return IntStream.rangeClosed(NOTE_MINIMALE, NOTE_MAXIMALE).boxed().toList();
    }

    public Integer getId() {
        return id;
    }

    public Integer getNote() {
        return note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public LocalDateTime getDateEvaluation() {
        return dateEvaluation;
    }

    public Incident getIncident() {
        return incident;
    }

}
