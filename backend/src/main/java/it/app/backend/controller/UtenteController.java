package it.app.backend.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.app.backend.service.UtenteService;

import it.app.backend.model.LoginRequest;
import it.app.backend.model.Utente;
import it.app.backend.model.UtenteResponse;
import it.app.backend.model.responseError;
import it.app.backend.model.RegistrationRequest;
import it.app.backend.model.UpdateRequest;


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
    public ResponseEntity<UtenteResponse> getByUsername(@PathVariable("username") String username){
        try {
            Optional<Utente> utente = service.findByUsername(username);

            if(utente.isPresent()){
                Utente ut = utente.get();
                return ResponseEntity.ok().body(new UtenteResponse(ut.getEmail(), ut.getFotoProfilo(), ut.getPhotoType()));
            }else{
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); 
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Void> verifyUtente(@RequestBody LoginRequest credentials){
        if(!service.verifyLogin(credentials.getUsername(), credentials.getPassword()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        else
            return ResponseEntity.accepted().build();
    }

    @PostMapping("/register")
    public ResponseEntity<responseError> registerUtente(
        @RequestParam String username,
        @RequestParam String email,
        @RequestParam String password,
        @RequestParam(required=false) MultipartFile photo,
        @RequestParam(required=false) String photoType){
        try {
            byte[] photoContent = null;
            String actualPhotoType = null; 

            if(photo != null){
                photoContent = photo.getBytes();
                actualPhotoType = photoType;
            }

            Utente registeredUtente = service.register( new RegistrationRequest(username, email, password, photoContent, actualPhotoType));
            if(registeredUtente != null)
                return ResponseEntity.status(HttpStatus.CREATED).build();
            else // user already exists
                return ResponseEntity.status(HttpStatus.CONFLICT).build(); 
        } catch (IllegalArgumentException e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        } catch(IOException e){
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<responseError> updateUtente(
        @PathVariable("username") String username, 
        @RequestParam(required=false) String email, 
        @RequestParam(required=false) MultipartFile photo, 
        @RequestParam(required=false) String photoType){
        try {
            byte[] photoContent = null;
            String actualPhotoType = null; 
            if(photo != null){
                photoContent = photo.getBytes();
                actualPhotoType = photoType;
            }
            Utente updatedUtente = service.update(username, new UpdateRequest(email, photoContent, actualPhotoType));

            if(updatedUtente == null)
                return ResponseEntity.notFound().build();
            else
                return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        } catch (IOException e){
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @PutMapping("/{username}/password")
    public ResponseEntity<String> updatePassword(@PathVariable("username") String username, @RequestParam String passwToUpdate){
        try {
            Utente updatedUtente = service.updatePassword(username, passwToUpdate);

            if(updatedUtente == null)
                return ResponseEntity.notFound().build();
            else
                return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
