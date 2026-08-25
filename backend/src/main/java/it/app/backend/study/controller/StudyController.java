package it.app.backend.study.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.app.backend.common.responseError;
import it.app.backend.study.model.CardDTO;
import it.app.backend.study.model.DeckDTO;
import it.app.backend.study.model.DeckData;
import it.app.backend.study.model.DoubleSidedCard;
import it.app.backend.study.model.DoubleSidedCardDTO;
import it.app.backend.study.model.QuizCard;
import it.app.backend.study.model.QuizCardDTO;
import it.app.backend.study.model.QuizOption;
import it.app.backend.study.model.QuizOptionDTO;
import it.app.backend.study.model.TrueFalseCard;
import it.app.backend.study.model.TrueFalseCardDTO;
import it.app.backend.study.service.CardService;
import it.app.backend.study.service.DeckService;

@RestController
@RequestMapping("/api/study")
public class StudyController {
    @Autowired
    public DeckService deckService;
    @Autowired 
    public CardService cardService;

    /* Deck related */
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

    /* Cards related */

    // get all deck cards
    @GetMapping("deck/{deckId}/cards")
    public ResponseEntity<List<CardDTO>> getAllDeckCards(@PathVariable("deckId") UUID deckId){
        try {
            return ResponseEntity.ok().body(cardService.getAllDeckCards(deckId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // get a deck card
    @GetMapping("card/{cardId}/double-sided")
    public ResponseEntity<DoubleSidedCardDTO> getDoubleSidedCard(@PathVariable("cardId") UUID cardId){
        try {
            DoubleSidedCard card = cardService.getDoubleSidedCard(cardId);
            return ResponseEntity.ok().body(new DoubleSidedCardDTO(card.getFront(), card.getBack()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("card/{cardId}/true-false")
    public ResponseEntity<TrueFalseCardDTO> getTrueFalse(@PathVariable("cardId") UUID cardId){
        try {
            TrueFalseCard card = cardService.getTrueFalseCard(cardId);
            return ResponseEntity.ok().body(new TrueFalseCardDTO(card.getFront(), card.getSolution()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("card/{cardId}/quiz")
    public ResponseEntity<QuizCardDTO> getQuiz(@PathVariable("cardId") UUID cardId){
        try {
            QuizCard card = cardService.getQuizCard(cardId);
            List<QuizOption> options = cardService.getQuizOptions(cardId);

            List<QuizOptionDTO> optionsDTO = new ArrayList<>();
            options.forEach(opt -> 
                optionsDTO.add(new QuizOptionDTO(opt.getText(), opt.getIsValid()))
            );

            return ResponseEntity.ok().body(new QuizCardDTO(card.getFront(), optionsDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // register card
    @PostMapping("deck/{deckId}/card/double-sided")
    public ResponseEntity<responseError> registerDoubleSidedCard(
        @PathVariable("deckId") UUID deckId, 
        @RequestBody DoubleSidedCardDTO cardData
    ){
        try {
            cardService.registerDoubleSidedCard(deckId, cardData);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @PostMapping("deck/{deckId}/card/true-false")
    public ResponseEntity<responseError> registerTrueFalseCard(
        @PathVariable("deckId") UUID deckId, 
        @RequestBody TrueFalseCardDTO cardData
    ){
        try {
            cardService.registerTrueFalseCard(deckId, cardData);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @PostMapping("deck/{deckId}/card/quiz")
    public ResponseEntity<responseError> registerQuizCard(
        @PathVariable("deckId") UUID deckId, 
        @RequestBody QuizCardDTO cardData
    ){
        try {
            cardService.registerQuizCard(deckId, cardData);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    // update card
    @PutMapping("card/{cardId}/double-sided")
    public ResponseEntity<responseError> updateDoubleSidedCard(
        @PathVariable("cardId") UUID cardId, 
        @RequestBody DoubleSidedCardDTO cardData
    ){
        try {
            cardService.updateDoubleSidedCard(cardId, cardData);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @PutMapping("card/{cardId}/true-false")
    public ResponseEntity<responseError> updateTrueFalseCard(
        @PathVariable("cardId") UUID cardId, 
        @RequestBody TrueFalseCardDTO cardData
    ){
        try {
            cardService.updateTrueFalseCard(cardId, cardData);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @PutMapping("card/{cardId}/quiz")
    public ResponseEntity<responseError> updateQuizCard(
        @PathVariable("cardId") UUID cardId, 
        @RequestBody QuizCardDTO cardData
    ){
        try {
            cardService.updateQuizCard(cardId, cardData);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    // delete card (& options)
    @DeleteMapping("card/quiz/option")
    public ResponseEntity<responseError> deleteOption(@RequestParam UUID optionId, @RequestParam String text){
        try {
            cardService.deleteQuizOption(optionId, text);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @DeleteMapping("card/quiz/{cardId}")
    public ResponseEntity<responseError> deleteQuizCard(@PathVariable("cardId") UUID cardId){
        try {
            cardService.deleteQuizCard(cardId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @DeleteMapping("card/true-false/{cardId}")
    public ResponseEntity<responseError> deleteTrueFalseCard(@PathVariable("cardId") UUID cardId){
        try {
            cardService.deleteTrueFalseCard(cardId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @DeleteMapping("card/double-sided/{cardId}")
    public ResponseEntity<responseError> deleteDoubleSidedCard(@PathVariable("cardId") UUID cardId){
        try {
            cardService.deleteDoubleSidedCard(cardId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }
}
