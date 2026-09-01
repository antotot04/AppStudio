package it.app.backend.study.model;

/* used in option registration */
public class QuizOptionData {
    private String answerText;
    private boolean validity;

    public QuizOptionData() {
    }

    public QuizOptionData(String answerText, boolean validity) throws IllegalArgumentException {
        if(answerText == null){
            throw new IllegalArgumentException("answerText is null");
        }
        this.answerText = answerText;
        this.validity = validity;
    }

    public String getAnswerText() {
        return answerText;
    }

    public boolean getValidity() {
        return validity;
    }

    public void setAnswerText(String answerText) throws IllegalArgumentException {
        if(answerText == null){
            throw new IllegalArgumentException("answerText is null");
        }
        this.answerText = answerText;
    }

    public void setValidity(boolean validity) {
        this.validity = validity;
    }
}
