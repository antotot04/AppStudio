package it.app.backend.study.model;

import java.util.UUID;

/* used to retreve an option */
public class QuizOptionDTO {
    private String answerText;
    private boolean validity;
    private UUID idOption;

    public QuizOptionDTO() {
    }

    public QuizOptionDTO(UUID idOption, String answerText, boolean validity) throws IllegalArgumentException{
        this.idOption = idOption;

        if(answerText == null){
            throw new IllegalArgumentException("answerText is null");
        }
        this.answerText = answerText;

        this.validity = validity;
    }

    public UUID getIdOption() {
        return idOption;
    }

    public String getAnswerText() {
        return answerText;
    }

    public boolean getValidity(){
        return validity;
    }

    public void setIdOption(UUID idOption) throws IllegalArgumentException {
        if(idOption == null){
            throw new IllegalArgumentException("idOption is null");
        }
        this.idOption = idOption;
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