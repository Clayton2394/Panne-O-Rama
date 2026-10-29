package grouph.panneorama.exception;

public class StationNonTrouveeException extends RuntimeException {
    public StationNonTrouveeException(Integer id) {
        super("La station " + id + " est introuvable.");
    }
}
