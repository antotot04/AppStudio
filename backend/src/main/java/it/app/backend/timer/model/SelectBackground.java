package it.app.backend.timer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="SELEZIONA_BACKGROUND", schema="public")
public class SelectBackground {

    @Id
    private String username;

    @OneToOne
    @MapsId
    @JoinColumn(name="Username", referencedColumnName="Username", nullable=false)
    private Settings settings;

    @ManyToOne(optional=false)
    @JoinColumn(name="Id_Suono_Background", referencedColumnName="Id_Suono")
    private Suono backgroundSound;

    @Column(name="Volume_Suono_Background")
    private int backgroundVolume;

    @Column(name="Attivo", nullable=false)
    private boolean isActive;
    
    public String getUsername() {
        return username;
    }

    public Settings getSettings() {
        return settings;
    }

    public Suono getBackgroundSound() {
        return backgroundSound;
    }

    public int getBackgroundVolume() {
        return backgroundVolume;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void setBackgroundSound(Suono backgroundSound) {
        this.backgroundSound = backgroundSound;
    }

    public void setBackgroundVolume(int backgroundVolume) {
        this.backgroundVolume = backgroundVolume;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

}
