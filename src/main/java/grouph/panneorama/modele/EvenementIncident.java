package grouph.panneorama.modele;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "evenement_incident")
public class EvenementIncident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private TypeEvenementIncident type;

    @Column(name = "date_evenement", nullable = false)
    private LocalDateTime dateEvenement;

    // Le login est conservé en texte : le journal doit survivre à une suppression de l'utilisateur.
    @Column(name = "auteur", nullable = false, length = 50)
    private String auteur;

    @Column(name = "detail", nullable = false, length = 500)
    private String detail;

    protected EvenementIncident() {
    }

    public EvenementIncident(Incident incident, TypeEvenementIncident type,
                             LocalDateTime dateEvenement, String auteur, String detail) {
        this.incident = incident;
        this.type = type;
        this.dateEvenement = dateEvenement;
        this.auteur = auteur;
        this.detail = detail;
    }

    public Integer getId() {
        return id;
    }

    public Incident getIncident() {
        return incident;
    }

    public TypeEvenementIncident getType() {
        return type;
    }

    public LocalDateTime getDateEvenement() {
        return dateEvenement;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getDetail() {
        return detail;
    }
}