package it.app.backend.study.model;

import java.util.UUID;

public class CardDTO {
    private UUID cardId;
    private String front;
    private String layout;

    public CardDTO() {
    }

    public CardDTO(UUID cardId, String front, String layout) throws IllegalArgumentException {
        if(cardId == null){
            throw new IllegalArgumentException("card id is null");
        }
        this.cardId = cardId;
        
        if(front == null){
            throw new IllegalArgumentException("front is null");
        }
        this.front = front;

        if(layout == null || 
        (!"quiz".equals(layout) && 
        !"double-sided".equals(layout) && 
        !"true-false".equals(layout))){
            throw new IllegalArgumentException("invalid card layout");
        }
        this.layout = layout;
    }

    public UUID getCardId() {
        return cardId;
    }

    public String getFront() {
        return front;
    }

    public String getLayout() {
        return layout;
    }

    public void setCardId(UUID cardId) throws IllegalArgumentException {
        if(cardId == null){
            throw new IllegalArgumentException("card id is null");
        }
        this.cardId = cardId;
    }

    public void setFront(String front) throws IllegalArgumentException {
        if(front == null){
            throw new IllegalArgumentException("front is null");
        }
        this.front = front;
    }

    public void setLayout(String layout) throws IllegalArgumentException {
        if(layout == null || 
        (!"quiz".equals(layout) && 
        !"double-sided".equals(layout) && 
        !"true-false".equals(layout))){
            throw new IllegalArgumentException("invalid card layout");
        }
        this.layout = layout;
    }
}
