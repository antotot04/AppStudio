package it.app.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity // Attiva la sicurezza web di Spring
public class SecurityConfig {

    // definisco l'algoritmo di cifratura utilizzato per cifrare le password degli utenti
    @Bean 
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // creo una catena di filtri per configurare i permessi sulle richieste HTTP 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
                // chiunque può fare l'accesso alle api REST di registrazione e di login
                .requestMatchers("/api/utenti/register", "/api/utenti/login").permitAll()
                // Qualsiasi altra richiesta non specificata sopra richiede obbligatoriamente l'autenticazione
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
