package it.app.backend.study.model;

import java.util.List;

public class QuizCardDTO {
    private String front;
    private List<QuizOptionDTO> options;

    public QuizCardDTO() {
    }

    public QuizCardDTO(String front, List<QuizOptionDTO> options) throws  IllegalArgumentException {
        if(front == null || front.length() > 600){
            throw new IllegalArgumentException("invalid front");
        }
        this.front = front;

        if(options == null){
            throw new IllegalArgumentException("options list is null");
        }
        this.options = options;
    }

    public String getFront() {
        return front;
    }

    public List<QuizOptionDTO> getOptions() {
        return options;
    }

    public void setFront(String front) throws IllegalArgumentException {
        if(front == null || front.length() > 600){
            throw new IllegalArgumentException("invalid front");
        }
        this.front = front;
    }

    public void setOptions(List<QuizOptionDTO> options) throws IllegalArgumentException {
        if(options == null){
            throw new IllegalArgumentException("options list is null");
        }
        this.options = options;
    }
    
}
