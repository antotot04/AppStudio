package it.app.backend.activity.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.app.backend.activity.model.Activity;
import it.app.backend.activity.model.ActivityDTO;
import it.app.backend.activity.model.ActivityData;
import it.app.backend.activity.model.ActivityPomo;
import it.app.backend.activity.repository.ActivityPomoRepository;
import it.app.backend.activity.repository.ActivityRepository;
import it.app.backend.timer.model.Pomodoro;
import it.app.backend.timer.service.PomodoroService;
import it.app.backend.utente.model.Utente;
import it.app.backend.utente.service.UtenteService;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository actRepo;
    @Autowired
    private ActivityPomoRepository pomoRepo;
    @Autowired
    private PomodoroService stdPomoService;
    @Autowired
    private UtenteService userService;


    private int getActivityPomoCurrCount(UUID activityId) throws IllegalArgumentException {
        if(activityId == null || !actRepo.existsById(activityId)){
            throw new IllegalArgumentException("Invalid activityId");
        }
        return pomoRepo.getCurrPomoCount(activityId).size();
    }

    public List<ActivityDTO> getAllUserActivities(String username) throws IllegalArgumentException {
        if(username == null){
            throw new IllegalArgumentException("username is null");
        }

        if(userService.findByUsername(username).isEmpty()){
            throw new IllegalArgumentException("user doesn't exists");
        }

        List<ActivityDTO> dtoList = new ArrayList<>();

        actRepo.findAllByUsername(username).forEach(activity -> {
            UUID id = activity.getId();
            String title = activity.getTitle();
            String description = activity.getDescription();
            int pomoCounter = activity.getActivityPomos();
            int currPomoCount = getActivityPomoCurrCount(id);
            dtoList.add(new ActivityDTO(id, title, description, pomoCounter, currPomoCount));
        });

        return dtoList;
    }

    public void registerActivity(String username, ActivityData data) throws IllegalArgumentException {
        if(username == null){
            throw new IllegalArgumentException("username is null");
        }

        Utente user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("user doesn't exists"));

        Activity actToReg = new Activity();
        actToReg.setUtente(user);
        actToReg.setActivityPomos(data.getPomoCounter());
        actToReg.setTitle(data.getTitle());
        actToReg.setDescription(data.getDescription());

        actRepo.save(actToReg);
    }

    public void registerFullActivityPomo(String username, UUID activityId, Instant timestamp) throws IllegalArgumentException {
        if(username == null || userService.findByUsername(username).isEmpty()){
            throw new IllegalArgumentException("username not valid");
        }

        Activity act = actRepo.findById(activityId).orElseThrow(() -> new IllegalArgumentException("activity not found"));
        if(act.getActivityPomos() <= getActivityPomoCurrCount(activityId)){
            throw new IllegalArgumentException("can't register another pomodoro bounded to this activity");
        }

        // register instance on global pomodoro table
        Pomodoro pomo = stdPomoService.registerUserPomo(username, timestamp);

        ActivityPomo pomoToReg = new ActivityPomo();
        pomoToReg.setPomodoro(pomo);
        pomoToReg.setActivity(act);

        pomoRepo.save(pomoToReg);
    }

    public void updateActivity(UUID activityId, ActivityData data) throws IllegalArgumentException {
        if(activityId == null || !actRepo.existsById(activityId)){
            throw new IllegalArgumentException("Invalid activityId");
        }

        if(getActivityPomoCurrCount(activityId) > data.getPomoCounter()){
            throw new IllegalArgumentException("invalid new total pomodoro counter");
        }

        Activity actToUpdate = actRepo.findById(activityId).orElseThrow(() -> new IllegalArgumentException("activity not found"));
        actToUpdate.setTitle(data.getTitle());
        actToUpdate.setDescription(data.getDescription());
        actToUpdate.setActivityPomos(data.getPomoCounter());
        
        actRepo.save(actToUpdate);
    }

    public void deleteActivity(UUID activityId){
        actRepo.deleteById(activityId);
    }

}
