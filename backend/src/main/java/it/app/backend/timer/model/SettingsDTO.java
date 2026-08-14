package it.app.backend.timer.model;

import java.util.UUID;

public class SettingsDTO {
    private int shortPause;
    private int longPause;
    private int longFreq;
    private UUID ringtone;
    private int ringtoneVolume;

    public SettingsDTO() {}

    public SettingsDTO(int shortPause, int longPause, int longFreq, UUID ringtone, int ringtoneVolume){
        this.shortPause = shortPause; 
        this.longPause = longPause;
        this.longFreq = longFreq; 
        this.ringtone = ringtone;
        this.ringtoneVolume = ringtoneVolume;
    }

    public int getLongFreq() {
        return longFreq;
    }
    public int getLongPause() {
        return longPause;
    }
    public UUID getRingtone() {
        return ringtone;
    }
    public int getRingtoneVolume() {
        return ringtoneVolume;
    }
    public int getShortPause() {
        return shortPause;
    }

    public void setLongFreq(int longFreq) {
        this.longFreq = longFreq;
    }
    public void setLongPause(int longPause) {
        this.longPause = longPause;
    }
    public void setRingtone(UUID ringtone) {
        this.ringtone = ringtone;
    }
    public void setRingtoneVolume(int ringtoneVolume) {
        this.ringtoneVolume = ringtoneVolume;
    }
    public void setShortPause(int shortPause) {
        this.shortPause = shortPause;
    }
}
