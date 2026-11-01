package grouph.panneorama.formulaire;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DescriptionIncidentFormulaire {

    @NotBlank(message = "La description de la panne est obligatoire")
    @Size(min = 10, max = 1000, message = "La description doit contenir entre 10 et 1000 caractères")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
