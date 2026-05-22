package it.app.backend.repository;

import it.app.backend.model.Utente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository 
// save(Entity), findById(Entity), findAll(), deleteById(Entity)
public interface UtenteRepository extends JpaRepository<Utente, String> {

    // email è una chiave candidata quindi aggiungo l'opzione di ricerca per email
    Optional<Utente> findByEmail(String email); 
}
