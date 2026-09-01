package it.app.backend.study.model;

import java.util.UUID;

import it.app.backend.utente.model.Utente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="MAZZO", schema="public")
public class Deck {
    @Id
    @Column(name="Id_Mazzo")
    private UUID id = UUID.randomUUID();

    @Column(name="Nome", length=50, nullable=false)
    private String title;

    @Column(name="Layout", length=17)
    private String layout;

    @ManyToOne(optional=false)
    @JoinColumn(name="Username", referencedColumnName="Username", nullable=false)
    private Utente Utente;

    public UUID getId() {
        return id;
    }

    public String getLayout() {
        return layout;
    }

    public Utente getUtente() {
        return Utente;
    }

    public String getTitle() {
        return title;
    }

    public void setLayout(String layout) throws IllegalArgumentException {
        if("quiz".equals(layout) ||
        "double-sided".equals(layout) ||
        "true-false".equals(layout)){
            this.layout = layout;
        }else if(layout == null || layout.equals("general")){
            this.layout = null;
        }else{
            throw new IllegalArgumentException("invalid deck layout");
        }
    }

    public void setTitle(String title) throws IllegalArgumentException {
        if(title == null){
            throw new IllegalArgumentException("deck title is null");
        }
        this.title = title;
    }

    public void setUtente(Utente Utente) throws IllegalArgumentException {
        if(Utente == null){
            throw new IllegalArgumentException("Utente is null");
        }
        this.Utente = Utente;
    }
}
