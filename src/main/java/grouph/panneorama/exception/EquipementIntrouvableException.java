package grouph.panneorama.exception;

public class EquipementIntrouvableException extends RessourceIntrouvableException {

    public EquipementIntrouvableException(Integer equipementId) {
        super("L'équipement " + equipementId + " n'existe pas");
    }
}
