package it.app.backend.timer.model;

public class UserSettings {
    private TimerDTO timer;
    private SuonoDTO suono;

    public UserSettings(TimerDTO timer, SuonoDTO suono){
        this.timer = timer;
        this.suono = suono;
    }

    public SuonoDTO getSuono() {
        return suono;
    }

    public TimerDTO getTimer() {
        return timer;
    }

    public void setSuono(SuonoDTO suono) {
        this.suono = suono;
    }

    public void setTimer(TimerDTO timer) {
        this.timer = timer;
    }
}
