package it.app.backend.study.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

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

import it.app.backend.common.responseError;
import it.app.backend.study.model.DeckDTO;
import it.app.backend.study.model.DeckData;
import it.app.backend.study.service.DeckService;

@RestController
@RequestMapping("/api/study")
public class StudyController {
    @Autowired
    public DeckService deckService;

    @GetMapping("{username}/decks")
    public ResponseEntity<List<DeckDTO>> getAllUserDecks(@PathVariable("username") String username){
        try {
            return ResponseEntity.ok().body(deckService.getAllUserDecks(username));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("deck/{id}")
    public ResponseEntity<DeckDTO> getUserDeck(@PathVariable("id") UUID id){
        try {
            return ResponseEntity.ok().body(deckService.getUserDeck(id).get());
        } catch (NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("{username}/deck")
    public ResponseEntity<responseError> registerUserDeck(@PathVariable("username") String username, @RequestBody DeckData data){
        try {
            deckService.registerUserDeck(username, data);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            responseError error = new responseError();
            error.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("deck/{id}")
    public ResponseEntity<responseError> updateDeck(@PathVariable("id") UUID id, @RequestBody DeckData updatedData){
        try {
            deckService.updateUserDeck(id, updatedData);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            responseError error = new responseError();
            error.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("deck/{id}")
    public ResponseEntity<responseError> deleteDeck(@PathVariable("id") UUID id){
        try {
            deckService.deleteUserDeck(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            responseError error = new responseError();
            error.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
