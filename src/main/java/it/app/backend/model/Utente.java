package it.app.backend.model;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name= "\"UTENTE\"") 
public class Utente {

    @Id
    @Column(name= "\"Username\"", length=30, nullable=false)
    private String username; // max 30 caratteri (da controllare nel service al salvataggio del dato)

    @Column(name= "\"Email\"", length=320, nullable=false, unique=true)
    private String email;

    @Column(name="\"Password\"", length=60, nullable=false)
    @JsonIgnore // Dice a Spring di ignorare questo campo quando trasforma l'oggetto della response in JSON
    private String password;

    @Column(name="\"Data_Creazione\"", nullable=false)
    private OffsetDateTime dataCreazione;

    @Column(name="\"Foto_Profilo\"")
    private byte[] fotoProfilo;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public OffsetDateTime getDataCreazione() {
        return dataCreazione;
    }

    public byte[] getFotoProfilo() {
        return fotoProfilo;
    }

    /* setter con controlli aggiuntivi per sicurezza */
    
    public void setUsername(String username) throws IllegalArgumentException{
        if(username != null && username.length() <= 30){
            this.username = username;
        }else
            throw new IllegalArgumentException("username non valido");
        
    }

    public void setEmail(String email) throws IllegalArgumentException{
        if(email != null && email.length() <= 320){
            this.email = email;
        }else
            throw new IllegalArgumentException("email non valida");
    }

    public void setPassword(String password) throws IllegalArgumentException{
        if(password != null && password.length() <= 60){
            this.password = password;
        }else
            throw new IllegalArgumentException("password non valida");
    }

    public void setDataCreazione(OffsetDateTime dataCreazione) {
        if(dataCreazione != null) 
            this.dataCreazione = dataCreazione;
    }

    public void setFotoProfilo(byte[] fotoProfilo) {
        this.fotoProfilo = fotoProfilo;
    }

    
}
