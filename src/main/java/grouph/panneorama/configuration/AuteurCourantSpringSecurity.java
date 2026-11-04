package grouph.panneorama.configuration;

import grouph.panneorama.service.AuteurCourant;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuteurCourantSpringSecurity implements AuteurCourant {
    private static final String AUTEUR_SYSTEME = "Système";

    @Override
    public String identifiant() {
        Authentication authentification = SecurityContextHolder.getContext().getAuthentication();
        if (authentification == null || authentification instanceof AnonymousAuthenticationToken) {
            return AUTEUR_SYSTEME;
        }
        return authentification.getName();
    }
}