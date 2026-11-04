package grouph.panneorama.exception;

public class NoteEvaluationInvalideException extends OperationInterditeException {

    public NoteEvaluationInvalideException(int noteMinimale, int noteMaximale) {
        super("La note doit être comprise entre " + noteMinimale + " et " + noteMaximale);
    }
}
