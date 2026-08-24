package it.app.backend.study.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.app.backend.study.model.Deck;
import it.app.backend.study.model.DeckDTO;
import it.app.backend.study.model.DeckData;
import it.app.backend.study.repository.DeckRepository;
import it.app.backend.utente.service.UtenteService;

@Service
public class DeckService {
    @Autowired
    private DeckRepository deckRepo;
    @Autowired
    private UtenteService utenteService;

    public List<DeckDTO> getAllUserDecks(String username) throws IllegalArgumentException{
        return deckRepo.findByUtente(username);
    }

    public Optional<DeckDTO> getUserDeck(UUID deckId) throws IllegalArgumentException, NoSuchElementException{
        Deck deck = deckRepo.findById(deckId).orElseThrow(() -> new NoSuchElementException("deck not found"));
        return Optional.of(new DeckDTO(deck.getId(), deck.getTitle(), deck.getLayout()));
    }

    public void registerUserDeck(String username, DeckData dataToRegister) throws IllegalArgumentException{
        Deck newDeck = new Deck();
        newDeck.setTitle(dataToRegister.getDeckTitle());
        newDeck.setLayout(dataToRegister.getDeckLayout());
        newDeck.setUtente(utenteService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("user not found"))
        );
        deckRepo.save(newDeck);
    }

    public void updateUserDeck(UUID deckId, DeckData updatedData) throws IllegalArgumentException{
        Deck deckToUpdate = deckRepo.findById(deckId)
            .orElseThrow(() -> new IllegalArgumentException("deck not found"));

        deckToUpdate.setTitle(updatedData.getDeckTitle());
        deckToUpdate.setLayout(updatedData.getDeckLayout());

        deckRepo.save(deckToUpdate);
    }

    public void deleteUserDeck(UUID id) throws IllegalArgumentException {
        if(!deckRepo.existsById(id)){
            throw new IllegalArgumentException("deck not found");
        }
        deckRepo.deleteById(id);
    }
}
