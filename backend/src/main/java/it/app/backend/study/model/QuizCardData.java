package it.app.backend.study.model;

import java.util.List;

/* dto for initial quiz card registration */
public class QuizCardData {
    private String front;
    private List<QuizOptionData> options;

    public QuizCardData() {
    }

    public QuizCardData(String front, List<QuizOptionData> options) throws  IllegalArgumentException {
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

    public List<QuizOptionData> getOptions() {
        return options;
    }

    public void setFront(String front) throws IllegalArgumentException {
        if(front == null || front.length() > 600){
            throw new IllegalArgumentException("invalid front");
        }
        this.front = front;
    }

    public void setOptions(List<QuizOptionData> options) throws IllegalArgumentException {
        if(options == null){
            throw new IllegalArgumentException("options list is null");
        }
        this.options = options;
    }
    
}
