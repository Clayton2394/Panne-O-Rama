package grouph.panneorama.modele;

public enum TypeEvenementIncident {
    DECLARATION("Déclaration"),
    MODIFICATION("Modification"),
    AFFECTATION("Affectation"),
    REAFFECTATION("Réaffectation"),
    RESOLUTION("Résolution"),
    EVALUATION("Évaluation");

    private final String libelle;

    TypeEvenementIncident(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}