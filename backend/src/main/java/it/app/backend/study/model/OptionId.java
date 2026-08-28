package it.app.backend.study.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class OptionId implements Serializable{
    private UUID idCard;
    private UUID idOption;

    public OptionId() {
    }

    public OptionId(UUID idCard, UUID idOption) {
        this.idCard = idCard;
        this.idOption = idOption;
    }

    public UUID getIdCard() {
        return idCard;
    }

    public UUID getIdOption() {
        return idOption;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null || getClass() != obj.getClass()){
            return false;
        }
        OptionId optId = (OptionId) obj;
        return idCard.equals(optId.getIdCard()) && idOption.equals(optId.getIdOption());
    }

    @Override
    public int hashCode(){
        return Objects.hash(idCard, idOption);
    }
}
