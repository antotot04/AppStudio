package it.app.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{username}")
    public ResponseEntity<Utente> getByUsername(@PathVariable("username") String username){
        try {
            Optional<Utente> utente = service.findByUsername(username);
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
    public ResponseEntity<Void> registerUtente(@RequestBody Utente newUtente){
        try {
            Utente registeredUtente = service.register(newUtente);
            if(registeredUtente != null)
                return ResponseEntity.status(HttpStatus.CREATED).build();
            else // user already exists
                return ResponseEntity.status(HttpStatus.CONFLICT).build(); 
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); 
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<Void> updateUtente(@PathVariable("username") String username, @RequestBody Utente dataToUpdate){
        try {
            Utente updatedUtente = service.update(username, dataToUpdate);

            if(updatedUtente == null)
                return ResponseEntity.notFound().build();
            else
                return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{username}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable("username") String username, @RequestBody String passwToUpdate){
        try {
            Utente updatedUtente = service.updatePassword(username, passwToUpdate);

            if(updatedUtente == null)
                return ResponseEntity.notFound().build();
            else
                return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUtente(@PathVariable("username") String username){
        try {
            service.deleteByUsername(username);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); 
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
