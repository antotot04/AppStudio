package it.app.backend.repository;

import it.app.backend.model.Utente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository 
// save(Entity), findById(Entity), findAll(), deleteById(Entity)
public interface UtenteRepository extends JpaRepository<Utente, String> {

    // Email is a candidate key, so I add the search option by email
    Optional<Utente> findByEmail(String email); 
}
