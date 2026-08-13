package it.app.backend.timer.model;

import java.util.UUID;

public class SuonoDTO {
    private UUID ringtone;
    private int ringtone_volume;
    private UUID background = null;
    private int background_volume = 30;

    public SuonoDTO(UUID ringtone, int ringtone_volume, UUID background, int background_volume) {
        this.ringtone = ringtone;
        this.ringtone_volume = ringtone_volume;
        this.background = background;
        this.background_volume = background_volume;
    }

    public SuonoDTO(UUID ringtone, int ringtone_volume) {
        this.ringtone = ringtone;
        this.ringtone_volume = ringtone_volume;
    }

    public UUID getBackground() {
        return background;
    }

    public int getBackground_volume() {
        return background_volume;
    }

    public UUID getRingtone() {
        return ringtone;
    }

    public int getRingtone_volume() {
        return ringtone_volume;
    }

    public void setBackground(UUID background) {
        this.background = background;
    }

    public void setBackground_volume(int background_volume) {
        this.background_volume = background_volume;
    }

    public void setRingtone(UUID ringtone) {
        this.ringtone = ringtone;
    }

    public void setRingtone_volume(int ringtone_volume) {
        this.ringtone_volume = ringtone_volume;
    }
}
