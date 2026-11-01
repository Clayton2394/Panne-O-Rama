package grouph.panneorama.exception;

public class IncidentResoluNonModifiableException extends OperationInterditeException {

    public IncidentResoluNonModifiableException(Integer incidentId) {
        super("L'incident " + incidentId + " est résolu : il ne peut plus être modifié");
    }
}
