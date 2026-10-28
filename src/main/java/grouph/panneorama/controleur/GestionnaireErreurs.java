package grouph.panneorama.controleur;

import grouph.panneorama.exception.RessourceIntrouvableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GestionnaireErreurs {
    private static final Logger log = LoggerFactory.getLogger(GestionnaireErreurs.class);

    @ExceptionHandler(RessourceIntrouvableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String traiterRessourceIntrouvable(RessourceIntrouvableException exception, Model model) {
        log.warn("Ressource introuvable : {}", exception.getMessage());
        model.addAttribute("messageErreur", exception.getMessage());
        return "erreur";
    }
}
