package grouph.panneorama.controleur;

import grouph.panneorama.exception.RessourceIntrouvableException;
import grouph.panneorama.exception.TransitionIncidentInterditeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(assignableTypes = IncidentAPIControleur.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GestionnaireErreursAPI {
    private static final Logger log = LoggerFactory.getLogger(GestionnaireErreursAPI.class);

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ProblemDetail traiterRessourceIntrouvable(RessourceIntrouvableException exception) {
        log.warn("API : ressource introuvable : {}", exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(TransitionIncidentInterditeException.class)
    public ProblemDetail traiterTransitionInterdite(TransitionIncidentInterditeException exception) {
        log.warn("API : transition interdite : {}", exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail traiterParametreInvalide(MethodArgumentTypeMismatchException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "La valeur '" + exception.getValue() + "' est invalide pour le paramètre '"
                        + exception.getName() + "'.");
    }
}