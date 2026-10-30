package grouph.panneorama.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class GenerateurHacheTest {

    @Test
    void afficherHaches() {
        BCryptPasswordEncoder encodeur = new BCryptPasswordEncoder();
        System.out.println("responsable : " + encodeur.encode("responsable"));
        System.out.println("agent       : " + encodeur.encode("agent"));
    }
}
