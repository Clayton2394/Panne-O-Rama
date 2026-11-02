package grouph.panneorama.exception;

public class PrestataireIntrouvableException extends RessourceIntrouvableException {

    public PrestataireIntrouvableException(Integer prestataireId) {
        super("Le prestataire " + prestataireId + " n'existe pas");
    }
}
