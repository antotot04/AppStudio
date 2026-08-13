package it.app.backend.timer.model;

public class TimerDTO {
    private int shortPause; // seconds
    private int longPause; // seconds
    private int frequency;

    public TimerDTO(int shortPause, int longPause, int frequency) {
        this.shortPause = shortPause;
        this.longPause = longPause;
        this.frequency = frequency;
    }

    public int getShortPause() {
        return shortPause;
    }

    public int getLongPause() {
        return longPause;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setShortPause(int shortPause) {
        this.shortPause = shortPause;
    }

    public void setLongPause(int longPause) {
        this.longPause = longPause;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
