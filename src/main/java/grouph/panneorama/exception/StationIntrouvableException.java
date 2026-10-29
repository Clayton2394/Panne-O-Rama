package grouph.panneorama.exception;

public class StationIntrouvableException extends RessourceIntrouvableException {

    public StationIntrouvableException(Integer stationId) {
        super("La station " + stationId + " n'existe pas");
    }
}
