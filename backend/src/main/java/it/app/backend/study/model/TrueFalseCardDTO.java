package it.app.backend.study.model;

public class TrueFalseCardDTO {
    private String front;
    private boolean validity;

    public TrueFalseCardDTO() {
    }

    public TrueFalseCardDTO(String front, boolean validity) throws IllegalArgumentException {
        if(front == null || front.length() > 600){
            throw new IllegalArgumentException("invalid front");
        }
        this.front = front;
        this.validity = validity;
    }

    public void setFront(String front) throws IllegalArgumentException {
        if(front == null || front.length() > 600){
            throw new IllegalArgumentException("invalid front");
        }
        this.front = front;
    }

    public void setValidity(boolean validity) {
        this.validity = validity;
    }

    public String getFront() {
        return front;
    }

    public boolean getValidity(){
        return validity;
    }
}
