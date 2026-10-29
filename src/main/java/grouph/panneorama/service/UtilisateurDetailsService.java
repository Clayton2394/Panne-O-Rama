package grouph.panneorama.service;

import grouph.panneorama.modele.UtilisateurConnecte;
import grouph.panneorama.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return utilisateurRepository.findByLogin(login)
                .map(UtilisateurConnecte::new)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur inconnu : " + login));
    }
}