package grouph.panneorama.controleur;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccueilControleur {

    @GetMapping("/")
    public String afficherAccueil() {
        return "accueil";
    }
}
