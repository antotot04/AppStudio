package it.app.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity // Enables Spring web security
public class SecurityConfig {

    // Define the encryption algorithm used to hash user passwords
    @Bean 
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Create a filter chain to configure permissions for HTTP requests
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
                // Anyone can access the REST registration and login endpoints + just allowing test endpoints to be reached 
                .requestMatchers("/api/utenti/register", "/api/utenti/login", "/api/utenti/wrongUtente", "/api/utenti/userToDelete").permitAll()
                // Access to http://127.0.0.1:8080/swagger-ui/index.html is allowed
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Any other request not specified above requires authentication
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
