package grouph.panneorama.exception;

public class IncidentResoluNonAffectableException extends OperationInterditeException {

    public IncidentResoluNonAffectableException(Integer incidentId) {
        super("L'incident " + incidentId + " est résolu : il ne peut plus être affecté ni réaffecté");
    }
}
