package grouph.panneorama.exception;

public class IncidentDejaEvalueException extends OperationInterditeException {

    public IncidentDejaEvalueException(Integer incidentId) {
        super("Le prestataire de l'incident " + incidentId + " a déjà été évalué");
    }
}
