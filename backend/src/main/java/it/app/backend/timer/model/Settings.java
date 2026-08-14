package it.app.backend.timer.model;

import it.app.backend.utente.model.Utente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="PREFERENZE_TIMER", schema="public")
public class Settings {
    @Id
    private String username; 

    @OneToOne
    @MapsId
    @JoinColumn(name = "Username", referencedColumnName = "Username", nullable=false)
    private Utente utente;

    @Column(name="Durata_Pausa_Corta")
    private String shortPause;

    @Column(name="Durata_Pausa_Lunga")
    private String longPause;

    @Column(name="Frequenza_Pausa_Lunga")
    private int longFreq;

    @ManyToOne(optional=false)
    @JoinColumn(name="Id_Suoneria", referencedColumnName = "Id_Suono", nullable=false)
    private Suono ringtone;
    
    @Column(name="Volume_Suoneria")
    private int ringtoneVolume;
    
    public String getUsername() {
        return username;
    }

    public String getShortPause() {
        return shortPause;
    }

    public int getLongFreq() {
        return longFreq;
    }

    public String getLongPause() {
        return longPause;
    }

    public int getRingtoneVolume() {
        return ringtoneVolume;
    }

    public Utente getUtente() {
        return utente;
    }

    public Suono getRingtone() {
        return ringtone;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLongFreq(int longFreq) {
        this.longFreq = longFreq;
    }

    public void setLongPause(String longPause) {
        this.longPause = longPause;
    }

    public void setRingtoneVolume(int ringtoneVolume) {
        this.ringtoneVolume = ringtoneVolume;
    }

    public void setShortPause(String shortPause) {
        this.shortPause = shortPause;
    }

    public void setRingtone(Suono ringtone) {
        this.ringtone = ringtone;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }
}
