package grouph.panneorama.modele;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UtilisateurConnecte implements UserDetails {

    private final Utilisateur utilisateur;

    public UtilisateurConnecte(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public String getPrenom() { return utilisateur.getPrenom(); }

    public String getLibelleProfil() { return utilisateur.getProfil().getLibelle(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + utilisateur.getProfil().name()));
    }

    @Override public String getPassword() { return utilisateur.getMotDePasse(); }
    @Override public String getUsername() { return utilisateur.getLogin(); }
}
