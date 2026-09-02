package it.app.backend.activity.model;

import java.util.UUID;

import it.app.backend.utente.model.Utente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="ATTIVITA", schema="public")
public class Activity {
    @Id
    @Column(name="Id_Attività")
    private UUID id = UUID.randomUUID();

    @Column(name="Titolo", length=100, nullable=false)
    private String title;

    @Column(name="Descrizione", columnDefinition="TEXT")
    private String description;

    @Column(name="Pomodori_Da_Completare", nullable=false)
    private Integer activityPomos;

    @ManyToOne
    @JoinColumn(name="Username", referencedColumnName="Username", nullable=false)
    private Utente utente;

    public Integer getActivityPomos() {
        return activityPomos;
    }

    public String getDescription() {
        return description;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setTitle(String title) throws IllegalArgumentException{
        if(title == null){
            throw new IllegalArgumentException("title can't be null");
        }
        if(title.length() > 100){
            throw new IllegalArgumentException("title is too long");
        }
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setActivityPomos(Integer activityPomos) throws IllegalArgumentException {
        if(activityPomos == null){
            throw new IllegalArgumentException("activityPomos can't be null");
        }
        if(activityPomos <= 0){
            throw new IllegalArgumentException("activityPomos can't be zero or negative");
        }
        this.activityPomos = activityPomos;
    }

    public void setUtente(Utente utente) throws IllegalArgumentException {
        if(utente == null){
            throw new IllegalArgumentException("utente can't be null");
        }
        this.utente = utente;
    }
}
