package grouph.panneorama.modele;

import jakarta.persistence.*;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "incident")
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "date_ouverture", nullable = false)
    private LocalDateTime dateOuverture;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 50)
    private StatutIncident statut;

    @Column(name = "date_resolution")
    private LocalDateTime dateResolution;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipement_id", nullable = false)
    private Equipement equipement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestataire_id")
    private Prestataire prestataire;

    protected Incident() {
    }

    // RG1 : un incident déclaré est rattaché à un équipement, daté, et démarre sans prestataire.
    public Incident(Equipement equipement, String description, LocalDateTime dateOuverture) {
        this.equipement = equipement;
        this.description = description;
        this.dateOuverture = dateOuverture;
        this.statut = StatutIncident.NON_AFFECTE;
    }

    public boolean estResolu() {
        return statut == StatutIncident.RESOLU;
    }

    public Duration dureeOuverture(Clock horloge) {
        LocalDateTime finOuverture = estResolu() ? dateResolution : LocalDateTime.now(horloge);
        return Duration.between(dateOuverture, finOuverture);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(LocalDateTime dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public StatutIncident getStatut() {
        return statut;
    }

    public void setStatut(StatutIncident statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateResolution() {
        return dateResolution;
    }

    public void setDateResolution(LocalDateTime dateResolution) {
        this.dateResolution = dateResolution;
    }

    public Equipement getEquipement() {
        return equipement;
    }

    public void setEquipement(Equipement equipement) {
        this.equipement = equipement;
    }

    public Prestataire getPrestataire() {
        return prestataire;
    }

    public void setPrestataire(Prestataire prestataire) {
        this.prestataire = prestataire;
    }

}
