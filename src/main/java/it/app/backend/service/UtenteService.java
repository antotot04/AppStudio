package it.app.backend.service;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.app.backend.model.Utente;
import it.app.backend.repository.UtenteRepository;


@Service
public class UtenteService {

    @Autowired
    private UtenteRepository repo;
    @Autowired
    private BCryptPasswordEncoder encoderPassword;


    private boolean containsNumbers(String passwd){
        for(char c : passwd.toCharArray()){
            if(Character.isDigit(c)) return true;
        }
        return false;
    }

    /* questo metodo permette di salvare un nuovo utente "newUtente" nel DB
     * se soddisfa i requisiti di base. Ritorna:
     * - null: in caso di utente già esistente 
     * - Utente: in caso di corretto salvataggio di "newUtente" 
     * Lancia "IllegalArgumentException" in caso di entità Utente non valida. 
     * Ciò si verifica quando "newUtente" o uno dei seguenti attributi è nullo
     * [username, email, password, dataCreazione] */
    public Utente register(Utente newUtente) throws IllegalArgumentException {
        // Controlli di base 
        String username = newUtente.getUsername();
        String passwordInChiaro = newUtente.getPassword();
        String email = newUtente.getEmail();
        if (newUtente == null || username == null 
            || email == null || passwordInChiaro == null) {
            throw new IllegalArgumentException("I campi obbligatori non possono essere nulli");
        }

        if (username.length() > 30) {
            throw new IllegalArgumentException("Lo username non può superare i 30 caratteri");
        }

        if (email.length() > 320) {
            throw new IllegalArgumentException("L'email è troppo lunga (max 320 caratteri)");
        }

        // Controllo sicurezza password in chiaro (Prima dell'hashing)
        if (passwordInChiaro.length() <= 6 || !containsNumbers(passwordInChiaro)) {
            throw new IllegalArgumentException("La password deve contenere più di 6 caratteri e almeno un numero");
        }

        // Cerco se esiste già utente nel db
        if (repo.existsById(username)) return null;

        // Cifratura (l'hash risultante sarà di 60 caratteri, perfetto per il db)
        String passwordCifrata = encoderPassword.encode(passwordInChiaro);
        newUtente.setPassword(passwordCifrata);
        newUtente.setDataCreazione(OffsetDateTime.now()); 

        return repo.save(newUtente);
    }

    /* qui verranno aggiornate le informazioni base (email e foto profilo)
     * il codice Ritorna:
     * - null: se utente non trovato
     * - utente: se trovato
     * Lancia "IllegalArgumentException" in caso di username nullo */
    public Utente update(String username, Utente updatedUtente) throws IllegalArgumentException{

        if(username == null || username.length() > 30)
            throw new IllegalArgumentException("username non valido");
        
        Utente utente = repo.findById(username).orElse(null);
        if(utente == null) return null;

        String newEmail = updatedUtente.getEmail();
        if(newEmail != null && newEmail.length() < 320 && !newEmail.equals(utente.getEmail())){ 
            if(!repo.findByEmail(newEmail).isPresent()){ 
                utente.setEmail(newEmail); 
            }else
                throw new IllegalArgumentException("Email già in uso da un altro utente");
        }

        byte[] newFotoProfilo = updatedUtente.getFotoProfilo();
        if(!Arrays.equals(utente.getFotoProfilo(), newFotoProfilo)){
            utente.setFotoProfilo(newFotoProfilo);
        }

        return repo.save(utente);
    }

    /* metodo per aggiornamento password. Controlla che la nuova password soddisfi 
     * i requisiti minimi di sicurezza e ricalcola un nuovo hash da sostituire 
     * a quello vecchio nel database. Ritorna:
     * - null: se utente non trovato
     * - utente: se trovato 
     * Lancia IllegalArgumentException in caso di username o password non validi */
    public Utente updatePassword(String username, String newPassword) throws IllegalArgumentException{
        if(username == null || username.length() > 30) 
            throw new IllegalArgumentException("username non valido");

        if(newPassword == null || newPassword.length() <= 6 || !containsNumbers(newPassword))
            throw new IllegalArgumentException("password non valida");

        Utente utente = repo.findById(username).orElse(null);
        if(utente == null) return null;

        if(encoderPassword.matches(newPassword, utente.getPassword())) return utente;

        utente.setPassword(encoderPassword.encode(newPassword));
        return repo.save(utente);
    }

    /* verifica credenziali utente */
    public boolean verifyLogin(String username, String password){
        if(username == null || password == null) return false;

        Utente utente = repo.findById(username).orElse(null);
        if(utente == null) return false;

        return encoderPassword.matches(password, utente.getPassword());
    }

    /* ricerca per email e per username e tutti */

    public Optional<Utente> findByUsername(String username)  throws IllegalArgumentException{
        if(username == null) 
            throw new IllegalArgumentException("username non valido");

        return repo.findById(username);
    }

    public Optional<Utente> findByEmail(String email) throws IllegalArgumentException{
        if(email == null) 
            throw new IllegalArgumentException("email non valida");

        return repo.findByEmail(email);
    }

    public List<Utente> findAll(){
        return repo.findAll();
    }

    /* eliminazione intero utente */
    public void deleteByUsername(String username)  throws IllegalArgumentException{
        if(username == null) 
            throw new IllegalArgumentException("username non valido");

        repo.deleteById(username);
    }
}
