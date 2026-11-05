package grouph.panneorama.exception;

import grouph.panneorama.modele.RegleSurveillance;

import java.util.Locale;

public class ConfirmationSurveillanceRequiseException extends OperationInterditeException {

    public ConfirmationSurveillanceRequiseException(String nomPrestataire) {
        super(String.format(Locale.FRANCE,
                "Le prestataire %s est %s : la moyenne de ses %d dernières évaluations est inférieure à %.1f. "
                        + "Confirmez l'affectation pour l'enregistrer.",
                nomPrestataire, RegleSurveillance.MENTION, RegleSurveillance.TAILLE_FENETRE,
                RegleSurveillance.SEUIL_MOYENNE));
    }
}
