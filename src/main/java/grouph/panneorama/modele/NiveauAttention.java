package grouph.panneorama.modele;

import java.time.Duration;

public enum NiveauAttention {
    ROUGE("Rouge", 3),
    ORANGE("Orange", 2),
    VERT("Vert", 1),
    PRIS_EN_CHARGE("Pris en charge", 0);

    public static final Duration SEUIL_ORANGE = Duration.ofHours(48);
    public static final Duration SEUIL_ROUGE = Duration.ofHours(72);

    private final String libelle;
    private final int priorite;

    NiveauAttention(String libelle, int priorite) {
        this.libelle = libelle;
        this.priorite = priorite;
    }

    // RG3 : vert sous 48 h, orange de 48 h incluses à moins de 72 h, rouge à partir de 72 h.
    public static NiveauAttention pourIncidentNonAffecte(Duration dureeOuverture) {
        if (dureeOuverture.compareTo(SEUIL_ROUGE) >= 0) {
            return ROUGE;
        }
        if (dureeOuverture.compareTo(SEUIL_ORANGE) >= 0) {
            return ORANGE;
        }
        return VERT;
    }

    public String getLibelle() {
        return libelle;
    }

    public int getPriorite() {
        return priorite;
    }
}