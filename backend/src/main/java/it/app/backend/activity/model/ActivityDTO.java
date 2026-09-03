package it.app.backend.activity.model;

import java.util.UUID;

// for activity data retrival
public class ActivityDTO {
    private UUID id;
    private String title;
    private String description;
    // total pomodoros
    private int pomoCounter;
    // current count of pomodoros
    private int currentPomos;

    public ActivityDTO(UUID id, String title, String description, int pomoCounter, int currentPomos) throws IllegalArgumentException {
        if(id == null){
            throw new IllegalArgumentException("id can't be null");
        }
        this.id = id;
        if(title == null){
            throw new IllegalArgumentException("title can't be null");
        }
        if(title.length() > 100){
            throw new IllegalArgumentException("title is too long");
        }
        this.title = title;
        this.description = description;
        if(pomoCounter <= 0){
            throw new IllegalArgumentException("pomoCounter can't be zero or negative");
        }
        this.pomoCounter = pomoCounter;
        if(currentPomos < 0 || currentPomos > this.pomoCounter){
            throw new IllegalArgumentException("currentPomos number is invalid");
        }
        this.currentPomos = currentPomos;
    }

    public String getDescription() {
        return description;
    }

    public UUID getId() {
        return id;
    }

    public int getPomoCounter() {
        return pomoCounter;
    }

    public String getTitle() {
        return title;
    }

    public int getCurrentPomos() {
        return currentPomos;
    }

    public void setId(UUID id) throws IllegalArgumentException {
        if(id == null){
            throw new IllegalArgumentException("id can't be null");
        }
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPomoCounter(int pomoCounter) throws IllegalArgumentException {
        if(pomoCounter <= 0){
            throw new IllegalArgumentException("pomoCounter can't be zero or negative");
        }
        this.pomoCounter = pomoCounter;
    }

    public void setTitle(String title) throws IllegalArgumentException {
        if(title == null){
            throw new IllegalArgumentException("title can't be null");
        }
        if(title.length() > 100){
            throw new IllegalArgumentException("title is too long");
        }
        this.title = title;
    }

    public void setCurrentPomos(int currentPomos) throws IllegalArgumentException {
        if(currentPomos < 0 || currentPomos > this.pomoCounter){
            throw new IllegalArgumentException("currentPomos number is invalid");
        }
        this.currentPomos = currentPomos;
    }
}
