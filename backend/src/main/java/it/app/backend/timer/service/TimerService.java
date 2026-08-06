package it.app.backend.timer.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import it.app.backend.timer.model.Pomodoro;
import it.app.backend.utente.model.Utente;
import it.app.backend.utente.repository.UtenteRepository;
import it.app.backend.timer.repository.TimerRepository;

public class TimerService {
    @Autowired
    private TimerRepository repo;
    
    @Autowired
    private UtenteRepository utenteRepo;


    public List<Pomodoro> findUserPomos(String username){
        return repo.findByUtenteUsername(username);
    }


    public void registerUserPomo(String username, Instant timestamp) throws IllegalArgumentException{
        Optional<Utente> optUtente = utenteRepo.findById(username);
        if(optUtente.isPresent()){
            Utente utente = optUtente.get();
            Pomodoro pomoToSave = new Pomodoro();
            pomoToSave.setTimestamp(timestamp);
            pomoToSave.setUtente(utente);
            repo.save(pomoToSave);
        }else{
            throw new IllegalArgumentException("invalid username");
        }
    }
}
