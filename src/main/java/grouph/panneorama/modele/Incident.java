package grouph.panneorama.modele;

import grouph.panneorama.exception.IncidentResoluNonAffectableException;
import grouph.panneorama.exception.IncidentResoluNonModifiableException;
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

    // F6 : seule la description reste modifiable tant que l'incident n'est pas résolu.
    public void verifierModifiable() {
        if (estResolu()) {
            throw new IncidentResoluNonModifiableException(id);
        }
    }

    public void modifierDescription(String nouvelleDescription) {
        verifierModifiable();
        this.description = nouvelleDescription;
    }

    public Duration dureeOuverture(Clock horloge) {
        LocalDateTime finOuverture = estResolu() ? dateResolution : LocalDateTime.now(horloge);
        return Duration.between(dateOuverture, finOuverture);
    }

    // RG5 : si résolu, ne peut plus être réaffecté.
    public void verifierAffectable() {
        if (estResolu()) {
            throw new IncidentResoluNonAffectableException(id);
        }
    }

    // F7 : affecter et réaffecter reviennent au même, le nouveau prestataire remplace l'éventuel précédent.
    public void affecterPrestataire(Prestataire nouveauPrestataire) {
        verifierAffectable();
        this.prestataire = nouveauPrestataire;
        this.statut = StatutIncident.AFFECTE;
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
