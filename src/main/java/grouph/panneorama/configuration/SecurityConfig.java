package grouph.panneorama.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/connexion", "/error", "/css/**", "/js/**", "/img/**").permitAll()
                        .requestMatchers("/tableau-de-bord/**").hasRole("RESPONSABLE")
                        .requestMatchers("/journal/**").hasRole("RESPONSABLE")
                        .requestMatchers("/stations/*/equipements/import").hasRole("RESPONSABLE")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/connexion")
                        .loginProcessingUrl("/connexion")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/connexion?erreur")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/deconnexion")
                        .logoutSuccessUrl("/connexion?deconnecte")
                        .invalidateHttpSession(true));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // L'API est consommée par des programmes : authentification à chaque requête, réponse 401 plutôt qu'une redirection.
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.PATCH, "/api/incidents/*/resolution").hasRole("RESPONSABLE")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
