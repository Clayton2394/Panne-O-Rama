package grouph.panneorama.formulaire;

import jakarta.validation.constraints.NotNull;

// F4 depuis l'accueil : l'équipement n'est pas connu par l'URL, il est choisi dans le formulaire.
// La description garde les mêmes règles de validation que la déclaration depuis la fiche équipement.
public class DeclarationIncidentFormulaire extends DescriptionIncidentFormulaire {

    @NotNull(message = "Veuillez choisir un équipement")
    private Integer equipementId;

    public Integer getEquipementId() {
        return equipementId;
    }

    public void setEquipementId(Integer equipementId) {
        this.equipementId = equipementId;
    }
}
