package grouph.panneorama.formulaire;

import jakarta.validation.constraints.NotNull;

public class AffectationPrestataireFormulaire {

    @NotNull(message = "Veuillez choisir un prestataire")
    private Integer prestataireId;

    // PS5 : case cochée par l'utilisateur pour confirmer l'affectation d'un prestataire sous surveillance.
    private boolean surveillanceConfirmee;

    public Integer getPrestataireId() {
        return prestataireId;
    }

    public void setPrestataireId(Integer prestataireId) {
        this.prestataireId = prestataireId;
    }

    public boolean isSurveillanceConfirmee() {
        return surveillanceConfirmee;
    }

    public void setSurveillanceConfirmee(boolean surveillanceConfirmee) {
        this.surveillanceConfirmee = surveillanceConfirmee;
    }
}
