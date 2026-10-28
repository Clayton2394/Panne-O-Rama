package grouph.panneorama.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

// L'horloge est injectée pour pouvoir figer « maintenant » dans les tests des seuils d'ouverture.
@Configuration
public class HorlogeConfiguration {

    @Bean
    public Clock horloge() {
        return Clock.systemDefaultZone();
    }
}
