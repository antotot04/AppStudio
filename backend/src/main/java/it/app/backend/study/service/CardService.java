package it.app.backend.study.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.app.backend.study.model.CardDTO;
import it.app.backend.study.model.Deck;
import it.app.backend.study.model.DoubleSidedCard;
import it.app.backend.study.model.DoubleSidedCardDTO;
import it.app.backend.study.model.OptionId;
import it.app.backend.study.model.QuizCard;
import it.app.backend.study.model.QuizCardDTO;
import it.app.backend.study.model.QuizOption;
import it.app.backend.study.model.QuizOptionDTO;
import it.app.backend.study.model.TrueFalseCard;
import it.app.backend.study.model.TrueFalseCardDTO;
import it.app.backend.study.repository.DeckRepository;
import it.app.backend.study.repository.DoubleSidedCardRepository;
import it.app.backend.study.repository.QuizCardRepository;
import it.app.backend.study.repository.QuizOptionRepository;
import it.app.backend.study.repository.TrueFalseCardRepository;

@Service
public class CardService {
    @Autowired
    private DoubleSidedCardRepository doubleSidedCardRepo;
    @Autowired
    private TrueFalseCardRepository trueFalseCardRepo;
    @Autowired
    private QuizCardRepository quizCardRepo;
    @Autowired
    private QuizOptionRepository quizOptionRepo;
    @Autowired
    private DeckRepository deckRepo;

    public List<CardDTO> getAllDeckCards(UUID deckId) throws IllegalArgumentException {

        if(!deckRepo.existsById(deckId)){
            throw new IllegalArgumentException("deckId is invalid");
        }

        List<CardDTO> cardList = new ArrayList<>();

        doubleSidedCardRepo.findByDeck(deckId).forEach((card) -> 
            cardList.add(new CardDTO(card.getId(), 
                card.getFront(), 
                "double-sided")
            )
        );

        trueFalseCardRepo.findByDeck(deckId).forEach((card) -> 
            cardList.add(new CardDTO(card.getId(), 
                card.getFront(), 
                "true-false")
            )
        );

        quizCardRepo.findByDeck(deckId).forEach((card) -> 
            cardList.add(new CardDTO(card.getId(), 
                card.getFront(), 
                "quiz")
            )
        );

        return cardList;
    }

    public DoubleSidedCard getDoubleSidedCard(UUID cardId) throws IllegalArgumentException {
        return doubleSidedCardRepo.findById(cardId).orElseThrow(() -> new IllegalArgumentException("invalid double sided card"));
    }

    public TrueFalseCard getTrueFalseCard(UUID cardId) throws IllegalArgumentException {
        return trueFalseCardRepo.findById(cardId).orElseThrow(() -> new IllegalArgumentException("invalid true false card"));
    }

    public QuizCard getQuizCard(UUID cardId) throws IllegalArgumentException {
        return quizCardRepo.findById(cardId).orElseThrow(() -> new IllegalArgumentException("invalid quiz card"));
    }

    public List<QuizOption> getQuizOptions(UUID cardId){
        return quizOptionRepo.findByCard(cardId);
    }

    public void registerDoubleSidedCard(UUID deckId, DoubleSidedCardDTO data) throws IllegalArgumentException {
        if(deckId == null || data == null){
            throw new IllegalArgumentException("null values on card registration");
        }

        Deck parentDeck = deckRepo.findById(deckId).orElseThrow(() -> 
            new IllegalArgumentException("deck does not exists. Cannot create card"));

        String deckLayout = parentDeck.getLayout();
        if(deckLayout != null && !deckLayout.equals("double-sided")){
            throw new IllegalArgumentException("Cannot register card on this deck: Wrong card layout for given deck");
        }

        DoubleSidedCard cardToRegister = new DoubleSidedCard();
        cardToRegister.setFront(data.getFront());
        cardToRegister.setBack(data.getBack());
        cardToRegister.setDeck(parentDeck);

        doubleSidedCardRepo.save(cardToRegister);
    }

    public void registerTrueFalseCard(UUID deckId, TrueFalseCardDTO data) throws IllegalArgumentException {
        if(deckId == null || data == null){
            throw new IllegalArgumentException("null values on card registration");
        }

        Deck parentDeck = deckRepo.findById(deckId).orElseThrow(() -> 
            new IllegalArgumentException("deck does not exists. Cannot create card"));

        String deckLayout = parentDeck.getLayout();
        if(deckLayout != null && !deckLayout.equals("true-false")){
            throw new IllegalArgumentException("Cannot register card on this deck: Wrong card layout for given deck");
        }

        TrueFalseCard cardToRegister = new TrueFalseCard();
        cardToRegister.setFront(data.getFront());
        cardToRegister.setSolution(data.getValidity());
        cardToRegister.setDeck(parentDeck);

        trueFalseCardRepo.save(cardToRegister);
    }

    public void registerQuizCard(UUID deckId, QuizCardDTO data) throws IllegalArgumentException {
        if(deckId == null || data == null){
            throw new IllegalArgumentException("null values on card registration");
        }

        Deck parentDeck = deckRepo.findById(deckId).orElseThrow(() -> 
            new IllegalArgumentException("deck does not exists. Cannot create card"));

        String deckLayout = parentDeck.getLayout();
        if(deckLayout != null && !deckLayout.equals("quiz")){
            throw new IllegalArgumentException("Cannot register card on this deck: Wrong card layout for given deck");
        }

        if(data.getOptions() == null || data.getOptions().isEmpty()){
            throw new IllegalArgumentException("No Options for this quiz card");
        }

        QuizCard cardToRegister = new QuizCard();
        cardToRegister.setFront(data.getFront());
        cardToRegister.setDeck(parentDeck);

        quizCardRepo.save(cardToRegister);

        for(QuizOptionDTO option : data.getOptions()){
            QuizOption optToRegister = new QuizOption();
            optToRegister.setCard(cardToRegister);
            optToRegister.setText(option.getAnswerText());
            optToRegister.setIsValid(option.getValidity());
            quizOptionRepo.save(optToRegister);
        }
    }

    public void updateDoubleSidedCard(UUID cardId, DoubleSidedCardDTO data) throws IllegalArgumentException {
        if(cardId == null || data == null){
            throw new IllegalArgumentException("null values on card update");
        }

        DoubleSidedCard card = doubleSidedCardRepo.findById(cardId).orElseThrow(() -> 
            new IllegalArgumentException("card doesn't exists")
        );

        card.setFront(data.getFront());
        card.setBack(data.getBack());

        doubleSidedCardRepo.save(card);
    }

    public void updateTrueFalseCard(UUID cardId, TrueFalseCardDTO data) throws IllegalArgumentException {
        if(cardId == null || data == null){
            throw new IllegalArgumentException("null values on card update");
        }

        TrueFalseCard card = trueFalseCardRepo.findById(cardId).orElseThrow(() -> 
            new IllegalArgumentException("card doesn't exists")
        );

        card.setFront(data.getFront());
        card.setSolution(data.getValidity());

        trueFalseCardRepo.save(card);
    }

    public void updateQuizCard(UUID cardId, QuizCardDTO data) throws IllegalArgumentException {
        if(cardId == null || data == null){
            throw new IllegalArgumentException("null values on card update");
        }

        QuizCard card = quizCardRepo.findById(cardId).orElseThrow(() -> 
            new IllegalArgumentException("card doesn't exists")
        );

        if(!card.getFront().equals(data.getFront())) {
            card.setFront(data.getFront());
            quizCardRepo.save(card);
        }

        for(QuizOptionDTO updatedOption : data.getOptions()){
            boolean updated = false;
            QuizOption option = quizOptionRepo.findById(new OptionId(cardId, updatedOption.getAnswerText())).orElseThrow(() -> 
                new IllegalArgumentException("option doesn't exists")
            );

            if(!option.getText().equals(updatedOption.getAnswerText())){
                updated = true;
                option.setText(updatedOption.getAnswerText());
            }
            
            if(option.getIsValid() != updatedOption.getValidity()){
                updated = true;
                option.setIsValid(updatedOption.getValidity());
            }

            if(updated){
                quizOptionRepo.save(option);
            }
        }
    }

    public void deleteQuizOption(UUID optionId, String text) throws IllegalArgumentException {
        OptionId id = new OptionId(optionId, text);
        if(!quizOptionRepo.existsById(id)){
            throw new IllegalArgumentException("cannot find option");
        }
        quizOptionRepo.deleteById(id);
    }

    public void deleteQuizCard(UUID cardId) throws IllegalArgumentException {
        if(!quizCardRepo.existsById(cardId)){
            throw new IllegalArgumentException("cannot find card");
        }
        quizCardRepo.deleteById(cardId);
    }

    public void deleteTrueFalseCard(UUID cardId) throws IllegalArgumentException {
        if(!trueFalseCardRepo.existsById(cardId)){
            throw new IllegalArgumentException("cannot find card");
        }
        trueFalseCardRepo.deleteById(cardId);
    }

    public void deleteDoubleSidedCard(UUID cardId) throws IllegalArgumentException {
        if(!doubleSidedCardRepo.existsById(cardId)){
            throw new IllegalArgumentException("cannot find card");
        }
        doubleSidedCardRepo.deleteById(cardId);
    }
}
