package grouph.panneorama.formulaire;

import jakarta.validation.constraints.NotNull;

public class AffectationPrestataireFormulaire {

    @NotNull(message = "Veuillez choisir un prestataire")
    private Integer prestataireId;

    public Integer getPrestataireId() {
        return prestataireId;
    }

    public void setPrestataireId(Integer prestataireId) {
        this.prestataireId = prestataireId;
    }
}
