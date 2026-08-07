package it.app.backend.timer.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.app.backend.timer.model.Pomodoro;
import it.app.backend.utente.model.Utente;
import it.app.backend.timer.repository.TimerRepository;
import it.app.backend.utente.service.UtenteService;

@Service
public class TimerService {
    @Autowired
    private TimerRepository repo;
    
    @Autowired
    private UtenteService utenteService;

    public void registerUserPomo(String username, Instant timestamp) throws IllegalArgumentException{
        Optional<Utente> optUtente = utenteService.findByUsername(username);
        if(optUtente.isPresent()){
            Utente utente = optUtente.get();
            Pomodoro pomoToSave = new Pomodoro();
            pomoToSave.setTimestamp(timestamp);
            pomoToSave.setUtente(utente);
            repo.save(pomoToSave);
        }else{
            throw new IllegalArgumentException("user not found");
        }
    }
}
