package it.app.backend.timer.model;

import java.util.UUID;

public class SoundTrack {
    private final UUID id;
    private String type;
    private String name;

    public SoundTrack(UUID id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}
