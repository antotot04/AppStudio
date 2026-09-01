package it.app.backend.study.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="CARTA_RISP_MULTIPLA", schema="public")
public class QuizCard {
    @Id
    @Column(name="Id_Carta")
    private final UUID id = UUID.randomUUID();

    @Column(name="Titolo", length=600, nullable=false)
    private String front; 

    @ManyToOne
    @JoinColumn(name="Id_Mazzo", referencedColumnName="Id_Mazzo", nullable=false)
    private Deck deck;

    public UUID getId() {
        return id;
    }

    public String getFront() {
        return front;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setFront(String front) throws IllegalArgumentException {
        if(front == null || front.length() > 600){
            throw new IllegalArgumentException("invalid front");
        }
        this.front = front;
    }

    public void setDeck(Deck deck) throws IllegalArgumentException {
        if(deck == null){
            throw new IllegalArgumentException("deck is null");
        }
        this.deck = deck;
    }
}