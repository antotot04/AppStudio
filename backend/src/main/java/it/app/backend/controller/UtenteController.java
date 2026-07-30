package it.app.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.app.backend.model.LoginRequest;
import it.app.backend.model.Utente;
import it.app.backend.service.UtenteService;


@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    @Autowired
    public UtenteService service;

    @GetMapping
    public ResponseEntity<List<Utente>> getAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll());
    }

    @GetMapping("/profile")
    public ResponseEntity<Utente> getByUsername(@AuthenticationPrincipal Utente userDetails){
        try {
            /* Use AuthenticationPrincipal from Spring Security to extract the currently logged-in user,
            avoiding passing the username via the URL. This way a user cannot perform a GET for
            another user's username. */
            String loggedInUser = userDetails.getUsername();
            Optional<Utente> utente = service.findByUsername(loggedInUser);
            return utente.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); 
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> verifyUtente(@RequestBody LoginRequest credentials){
        if(!service.verifyLogin(credentials.getUsername(), credentials.getPassword()))
            // Send a generic error message (for security) to the frontend
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("incorrect username or password");
        else
            return ResponseEntity.accepted().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Utente> registerUtente(@RequestBody Utente newUtente){
        try {
            Utente registeredUtente = service.register(newUtente);
            if(registeredUtente != null)
                return ResponseEntity.status(HttpStatus.CREATED).body(registeredUtente);
            else // user already exists
                return ResponseEntity.status(HttpStatus.CONFLICT).build(); 
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); 
        }
    }

    @PutMapping("/username")
    public ResponseEntity<Utente> updateUtente(@AuthenticationPrincipal Utente utente, @RequestBody Utente dataToUpdate){
        try {
            // For security, I take the username of the currently logged-in user
            Utente updatedUtente = service.update(utente.getUsername(), dataToUpdate);
            
            if(updatedUtente == null)
                return ResponseEntity.notFound().build();
            else
                return ResponseEntity.ok(updatedUtente);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/password")
    public ResponseEntity<Utente> updatePassword(@AuthenticationPrincipal Utente utente, @RequestBody Utente passwToUpdate){
        try {
            Utente updatedUtente = service.updatePassword(utente.getUsername(), passwToUpdate.getPassword());

            if(updatedUtente == null)
                return ResponseEntity.notFound().build();
            else
                return ResponseEntity.ok(updatedUtente);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteUtente(@AuthenticationPrincipal Utente utenteToDelete){
        try {
            service.deleteByUsername(utenteToDelete.getUsername());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); 
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
