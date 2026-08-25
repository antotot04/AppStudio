package it.app.backend.study.model;

public class QuizOptionDTO {
    private String answerText;
    private boolean validity;

    public QuizOptionDTO() {
    }

    public QuizOptionDTO(String answerText, boolean validity) throws IllegalArgumentException{
        if(answerText == null){
            throw new IllegalArgumentException("answerText is null");
        }
        this.answerText = answerText;

        this.validity = validity;
    }

    public String getAnswerText() {
        return answerText;
    }

    public boolean getValidity(){
        return validity;
    }

    public void setAnswerText(String answerText) throws IllegalArgumentException {
        if(answerText == null){
            throw new IllegalArgumentException("answerText is null");
        }
        this.answerText = answerText;
    }

    public void setValidity(boolean validity){
        this.validity = validity;
    }
}