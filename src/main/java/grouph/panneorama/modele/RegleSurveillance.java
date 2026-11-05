package grouph.panneorama.modele;

import java.util.List;
import java.util.Optional;

public final class RegleSurveillance {
    public static final int TAILLE_FENETRE = 5;
    public static final double SEUIL_MOYENNE = 3.0;
    public static final String MENTION = "sous surveillance";

    private RegleSurveillance() {
    }

    // PS2 : tant que le prestataire a moins de 5 évaluations, aucune moyenne n'est calculée.
    public static Optional<Double> moyenne(List<Integer> notesDuPlusRecentAuPlusAncien) {
        if (notesDuPlusRecentAuPlusAncien.size() < TAILLE_FENETRE) {
            return Optional.empty();
        }
        double moyenne = notesDuPlusRecentAuPlusAncien.stream()
                .limit(TAILLE_FENETRE)
                .mapToInt(Integer::intValue)
                .average()
                .orElseThrow();
        return Optional.of(moyenne);
    }

    // PS2 : sans moyenne, le prestataire n'est jamais sous surveillance. La comparaison au seuil est la PS3.
    public static boolean estSousSurveillance(List<Integer> notesDuPlusRecentAuPlusAncien) {
        return moyenne(notesDuPlusRecentAuPlusAncien)
                .map(valeur -> valeur < SEUIL_MOYENNE)
                .orElse(false);
    }
}