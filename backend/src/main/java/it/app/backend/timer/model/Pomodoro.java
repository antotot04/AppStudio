package it.app.backend.timer.model;

import java.time.Instant;
import java.util.UUID;

import it.app.backend.utente.model.Utente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name="POMODORO", schema="public")
public class Pomodoro {

    @Id
    @Column(name="Id_Pomodoro")
    private UUID Id_Pomodoro; 

    @Column(name="Timestamp", nullable=false)
    private Instant timestamp;

    @ManyToOne(optional = false)
    @JoinColumn(name = "Username", referencedColumnName = "Username", nullable=false)
    private Utente utente;

    public UUID getId_Pomodoro() {
        return Id_Pomodoro;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }
}
