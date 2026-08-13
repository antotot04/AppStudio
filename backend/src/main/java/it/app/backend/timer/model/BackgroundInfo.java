package it.app.backend.timer.model;

import java.util.UUID;

public class BackgroundInfo {
    private UUID backgroundSound; // uuid
    private int backgroundVolume;

    public BackgroundInfo() {
    }

    public BackgroundInfo(UUID backgroundSound, int backgroundVolume) {
        this.backgroundSound = backgroundSound;
        this.backgroundVolume = backgroundVolume;
    }

    public UUID getSound() {
        return backgroundSound;
    }

    public int getVolume() {
        return backgroundVolume;
    }

    public void setSound(UUID backgroundSound) {
        this.backgroundSound = backgroundSound;
    }

    public void setVolume(int backgroundVolume) {
        this.backgroundVolume = backgroundVolume;
    }
}
