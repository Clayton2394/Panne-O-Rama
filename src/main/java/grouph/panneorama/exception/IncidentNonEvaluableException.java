package grouph.panneorama.exception;

public class IncidentNonEvaluableException extends OperationInterditeException {

    public IncidentNonEvaluableException(Integer incidentId) {
        super("L'incident " + incidentId + " ne peut pas être évalué : il doit être résolu et avoir un prestataire affecté");
    }
}
