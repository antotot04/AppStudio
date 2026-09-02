package it.app.backend.timer.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.app.backend.timer.model.LeaderboardUser;
import it.app.backend.timer.model.Pomodoro;
import it.app.backend.utente.model.Utente;
import it.app.backend.timer.repository.PomodoroRepository;
import it.app.backend.utente.service.UtenteService;

@Service
public class PomodoroService {
    @Autowired
    private PomodoroRepository repo;
    
    @Autowired
    private UtenteService utenteService;

    public Pomodoro registerUserPomo(String username, Instant timestamp) throws IllegalArgumentException{
        Optional<Utente> optUtente = utenteService.findByUsername(username);
        if(optUtente.isPresent()){
            Utente utente = optUtente.get();
            Pomodoro pomoToSave = new Pomodoro();
            pomoToSave.setTimestamp(timestamp);
            pomoToSave.setUtente(utente);
            return repo.save(pomoToSave);
        }else{
            throw new IllegalArgumentException("user not found");
        }
    }

    public List<LeaderboardUser> getLeaderboard(int quantity, String time){
        if(time.equals("week")){
            return repo.getLeaderboard("7 days", quantity);
        }else if(time.equals("month")){
            return repo.getLeaderboard("1 month", quantity);
        }else{
            return repo.getLeaderboard("1 year", quantity);
        }
    }

    public LeaderboardUser getUserLeaderboard(String username, String time, int relativeQuantity){
        if(time.equals("week")){
            return repo.getUserLeaderboard("7 days", username, relativeQuantity);
        }else if(time.equals("month")){
            return repo.getUserLeaderboard("1 month", username, relativeQuantity);
        }else{
            return repo.getUserLeaderboard("1 year", username, relativeQuantity);
        }
    }
}
