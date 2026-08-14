package it.app.backend.utente.service;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.app.backend.utente.model.Utente;
import it.app.backend.timer.model.SettingsDTO;
import it.app.backend.timer.service.SettingsService;
import it.app.backend.utente.model.RegistrationRequest;
import it.app.backend.utente.model.UpdateRequest;
import it.app.backend.utente.repository.UtenteRepository;
import jakarta.transaction.Transactional;


@Service
public class UtenteService {

    @Autowired
    private UtenteRepository repo;
    @Autowired
    private SettingsService settingsService; 
    @Autowired
    private BCryptPasswordEncoder encoderPassword;


    private boolean containsNumbers(String passwd){
        for(char c : passwd.toCharArray()){
            if(Character.isDigit(c)) return true;
        }
        return false;
    }

    /* This method saves a new user in the DB along with its default timer settings if it meets the basic requirements. Returns:
     * - null: if the user already exists
     * - Utente: if the new user is saved correctly
     * Throws IllegalArgumentException if the user entity is invalid.
     * This happens when newUtente or any of the following attributes are null:
     * [username, email, password, dataCreazione] */
    @Transactional
    public Utente register(RegistrationRequest newUtente) throws IllegalArgumentException {
        // Basic checks
        String username = newUtente.getUsername();
        String passwordInClear = newUtente.getPassword();
        String email = newUtente.getEmail();
        if (newUtente == null || username == null 
            || email == null || passwordInClear == null) {
            throw new IllegalArgumentException("Required fields cannot be null");
        }

        if (username.length() > 30) {
            throw new IllegalArgumentException("The username cannot exceed 30 characters");
        }

        if (email.length() > 320) {
            throw new IllegalArgumentException("The email is too long (max 320 characters)");
        }

        // Check whether the email is unique or not
        if (repo.findByEmail(newUtente.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email already in use by another user");
        }

        // Password security check before hashing
        if (passwordInClear.length() <= 6 || !containsNumbers(passwordInClear)) {
            throw new IllegalArgumentException("The password must contain more than 6 characters and at least one number");
        }

        // Check whether the user already exists in the DB
        if (repo.existsById(username)) return null;

        /* registering data in user entity */
        Utente utenteToRegister = new Utente(); 

        utenteToRegister.setUsername(username);

        utenteToRegister.setEmail(email);

        byte[] profilePhoto = newUtente.getProfilePhoto();
        if(profilePhoto != null){
            utenteToRegister.setFotoProfilo(profilePhoto);
            utenteToRegister.setPhotoType(newUtente.getPhotoType());
        }

        // Password Encryption (the resulting hash will be 60 characters)
        String encryptedPassword = encoderPassword.encode(passwordInClear);
        newUtente.setPassword(encryptedPassword);
        utenteToRegister.setPassword(newUtente.getPassword());

        utenteToRegister.setDataCreazione(OffsetDateTime.now());
        Utente savedUtente = repo.save(utenteToRegister);

        // also saving settings for new utente
        settingsService.registerUserSettings(username, new SettingsDTO( // default values
            300,
            900,
            4,
            UUID.fromString("cb01f746-9e74-4832-9474-f9309724d32b"),
            70
        ));

        return savedUtente;
    }

    /* This is where the basic user information (email and profile picture) will be updated.
     * Returns:
     * - null: if the user is not found
     * - utente: if found
     * Throws IllegalArgumentException if the username is null */
    public Utente update(String username, UpdateRequest updatedUtente) throws IllegalArgumentException{

        if(username == null || username.length() > 30)
            throw new IllegalArgumentException("username not valid");
        
        Utente utente = repo.findById(username).orElse(null);
        if(utente == null) return null;

        String newEmail = updatedUtente.getEmail();
        if(newEmail != null && !newEmail.isBlank() && newEmail.length() < 320 && !newEmail.equals(utente.getEmail())){ 
            if(!repo.findByEmail(newEmail).isPresent()){ 
                utente.setEmail(newEmail); 
            }else
                throw new IllegalArgumentException("Email already in use by another user");
        }

        byte[] newFotoProfilo = updatedUtente.getProfilePhoto();
        String photoType = updatedUtente.getPhotoType();
        if(newFotoProfilo != null && photoType != null && !Arrays.equals(utente.getFotoProfilo(), newFotoProfilo)){
            utente.setFotoProfilo(newFotoProfilo);
            utente.setPhotoType(updatedUtente.getPhotoType());
        }

        return repo.save(utente);
    }

    /* Password update method. Checks that the new password satisfies the minimum security requirements
     * and recalculates a new hash to replace the old one in the database. Returns:
     * - null: if the user is not found
     * - utente: if found
     * Throws IllegalArgumentException if the username or password is invalid */
    public Utente updatePassword(String username, String newPassword) throws IllegalArgumentException{
        if(username == null || username.length() > 30) 
            throw new IllegalArgumentException("username not valid");

        if(newPassword == null || newPassword.length() <= 6 || !containsNumbers(newPassword))
            throw new IllegalArgumentException("password not valid");

        Utente utente = repo.findById(username).orElse(null);
        if(utente == null) return null;

        if(encoderPassword.matches(newPassword, utente.getPassword())) return utente;

        utente.setPassword(encoderPassword.encode(newPassword));
        return repo.save(utente);
    }

    /* Verify user credentials */
    public boolean verifyLogin(String username, String password){
        if(username == null || password == null) return false;

        Utente utente = repo.findById(username).orElse(null);
        if(utente == null) return false;

        return encoderPassword.matches(password, utente.getPassword());
    }

    /* Search by email and username */
    public Optional<Utente> findByUsername(String username)  throws IllegalArgumentException{
        if(username == null) 
            throw new IllegalArgumentException("username not valid");

        return repo.findById(username);
    }

    public Optional<Utente> findByEmail(String email) throws IllegalArgumentException{
        if(email == null) 
            throw new IllegalArgumentException("email not valid");

        return repo.findByEmail(email);
    }

    public List<Utente> findAll(){
        return repo.findAll();
    }

    /* Delete the entire user */
    public void deleteByUsername(String username)  throws IllegalArgumentException{
        if(username == null) 
            throw new IllegalArgumentException("username not valid");

        repo.deleteById(username);
    }
}
