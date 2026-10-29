package grouph.panneorama.modele;

public enum Profil {
    RESPONSABLE("Responsable d'exploitation"),
    AGENT("Agent de signalement");

    private final String libelle;

    Profil(String libelle) { this.libelle = libelle; }

    public String getLibelle() { return libelle; }
}
