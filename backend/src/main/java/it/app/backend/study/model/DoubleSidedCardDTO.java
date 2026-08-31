package it.app.backend.study.model;

public class DoubleSidedCardDTO {
    private String front;
    private String back;

    public DoubleSidedCardDTO() {
    }

    public DoubleSidedCardDTO(String front, String back) throws IllegalArgumentException {
        if(front == null || front.length() > 600){
            throw new IllegalArgumentException("invalid front");
        }
        this.front = front;

        if(back == null){
            throw new IllegalArgumentException("back is null");
        }
        this.back = back;

    }

    public String getBack() {
        return back;
    }

    public String getFront() {
        return front;
    }

    public void setBack(String back) throws IllegalArgumentException {
        if(back == null){
            throw new IllegalArgumentException("back is null");
        }
        this.back = back;
    }

    public void setFront(String front) throws IllegalArgumentException {
        if(front == null || front.length() > 600){
            throw new IllegalArgumentException("invalid front");
        }
        this.front = front;
    }
}
