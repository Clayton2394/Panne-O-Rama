package grouph.panneorama.modele;

import jakarta.persistence.*;

@Entity
@Table(name = "equipement")
public class Equipement {
    public static final int LONGUEUR_MAX_REFERENCE = 50;
    public static final int LONGUEUR_MAX_DESCRIPTION = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "reference", nullable = false, length = 50)
    private String reference;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private TypeEquipement type;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat_fonctionnement", nullable = false, length = 50)
    private EtatEquipement etatFonctionnement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    public Equipement() {
    }

    public Equipement(Station station, String reference, String description,
                      TypeEquipement type, EtatEquipement etatFonctionnement) {
        this.station = station;
        this.reference = reference;
        this.description = description;
        this.type = type;
        this.etatFonctionnement = etatFonctionnement;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeEquipement getType() {
        return type;
    }

    public void setType(TypeEquipement type) {
        this.type = type;
    }

    public EtatEquipement getEtatFonctionnement() {
        return etatFonctionnement;
    }

    public void setEtatFonctionnement(EtatEquipement etatFonctionnement) {
        this.etatFonctionnement = etatFonctionnement;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

}