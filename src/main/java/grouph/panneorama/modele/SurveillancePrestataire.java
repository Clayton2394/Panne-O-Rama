package grouph.panneorama.modele;

// Évolution « prestataire sous surveillance » : les paramètres de la règle sont définis ici et nulle part ailleurs (PS6).
public final class SurveillancePrestataire {
    // PS1 : seules les évaluations les plus récentes comptent, dans une fenêtre de cette taille.
    public static final int TAILLE_FENETRE = 5;

    private SurveillancePrestataire() {
    }
}
