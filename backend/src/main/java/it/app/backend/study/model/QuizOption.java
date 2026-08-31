package it.app.backend.study.model;

import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="OPZIONI_RISP_MULTIPLA", schema="public")
@IdClass(OptionId.class)
public class QuizOption {
    @Id
    private UUID idCard;

    @ManyToOne
    @MapsId("idCard")
    @JoinColumn(name="Id_Carta", referencedColumnName="Id_Carta", nullable=false)
    private QuizCard card;

    @Id
    @Column(name="Id_Opzione")
    private UUID idOption = UUID.randomUUID();

    @Column(name="Testo_Opzione", columnDefinition="TEXT")
    private String text;

    @Column(name="Validità_Opzione", nullable=false)
    private boolean isValid;

    public UUID getIdCard() {
        return idCard;
    }

    public UUID getIdOption() {
        return idOption;
    }

    public QuizCard getCard() {
        return card;
    }

    public String getText() {
        return text;
    }

    public boolean getIsValid(){
        return isValid;
    }

    public void setCard(QuizCard card) throws IllegalArgumentException {
        if(card == null){
            throw new IllegalArgumentException("card is null");
        }
        this.card = card;
    }

    public void setText(String text) throws IllegalArgumentException {
        if(text == null){
            throw new IllegalArgumentException("text is null");
        }
        this.text = text;
    }

    public void setIsValid(boolean isValid){
        this.isValid = isValid;
    }
    
}
