package it.app.backend.controller;

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

import it.app.backend.model.Utente;
import it.app.backend.service.UtenteService;


@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    @Autowired
    public UtenteService service;

    @GetMapping("/profilo")
    public ResponseEntity<Utente> getByUsername(@AuthenticationPrincipal Utente userDetails){
        try {
            /*sfrutto AuthenticationPrincipal di Spring Security per estrarre l'utente attualmente loggato
            evitando di passare lo username via URL. In questo modo un utente non potrà fare GET con
            Username di altri utenti */
            String utenteLoggato = userDetails.getUsername();
            Optional<Utente> utente = service.findByUsername(utenteLoggato);
            return utente.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); 
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> verifyUtente(@RequestBody Utente credenziali){
        if(!service.verifyLogin(credenziali.getUsername(), credenziali.getPassword()))
            // mando un messaggio d'errore generico (per sicurezza) al frontend 
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("username o password errati");
        else
            return ResponseEntity.accepted().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Utente> registerUtente(@RequestBody Utente newUtente){
        try {
            Utente registeredUtente = service.register(newUtente);
            if(registeredUtente != null)
                return ResponseEntity.status(HttpStatus.CREATED).body(registeredUtente);
            else // utente esiste già
                return ResponseEntity.badRequest().build(); 
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); 
        }
    }

    @PutMapping("/username")
    public ResponseEntity<Utente> updateUtente(@AuthenticationPrincipal Utente utente, @RequestBody Utente dataToUpdate){
        try {
            // per sicurezza, prendo lo username dell'utente attualmente loggato
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

    @DeleteMapping("/profilo")
    public ResponseEntity<Utente> deleteUtente(@AuthenticationPrincipal Utente utenteToDelete){
        try {
            service.deleteByUsername(utenteToDelete.getUsername());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); 
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
