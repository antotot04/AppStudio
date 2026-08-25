package it.app.backend.study.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class OptionId implements Serializable{
    private UUID id;
    private String text;

    public OptionId() {
    }

    public OptionId(UUID id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null || getClass() != obj.getClass()){
            return false;
        }
        OptionId optId = (OptionId) obj;
        return id.equals(optId.getId()) && text.equals(optId.getText());
    }

    @Override
    public int hashCode(){
        return Objects.hash(text, id);
    }
}
