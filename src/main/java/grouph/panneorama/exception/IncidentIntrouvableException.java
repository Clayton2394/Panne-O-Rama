package grouph.panneorama.exception;

public class IncidentIntrouvableException extends RessourceIntrouvableException {

    public IncidentIntrouvableException(Integer incidentId) {
        super("L'incident " + incidentId + " n'existe pas");
    }
}
