package it.app.backend.model;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name= "UTENTE", schema="public") 
public class Utente {

    @Id
    @Column(name= "Username", length=30, nullable=false)
    private String username; // maximum 30 characters (to be validated in the service when saving the data)

    @Column(name= "Email", length=320, nullable=false, unique=true)
    private String email;

    @Column(name="Password", length=60, nullable=false)
    private String password;

    @Column(name="Data_Creazione", nullable=false)
    private OffsetDateTime dataCreazione;

    @Column(name="Foto_Profilo")
    private byte[] fotoProfilo;

    @Column(name="Photo_Type", length=60)
    private String photoType;

    // constructors
    public Utente(){}

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

    public String getPhotoType() {
        return photoType;
    }

    /* setters with additional security checks */
    
    public void setUsername(String username) throws IllegalArgumentException{
        if(username != null && username.length() <= 30){
            this.username = username;
        }else
            throw new IllegalArgumentException("username not valid");
        
    }

    public void setEmail(String email) throws IllegalArgumentException{
        if(email != null && email.length() <= 320){
            this.email = email;
        }else
            throw new IllegalArgumentException("email not valid");
    }

    public void setPassword(String password) throws IllegalArgumentException{
        if(password != null && password.length() <= 60){
            this.password = password;
        }else
            throw new IllegalArgumentException("password not valid");
    }

    public void setDataCreazione(OffsetDateTime dataCreazione) {
        if(dataCreazione != null) 
            this.dataCreazione = dataCreazione;
    }

    public void setFotoProfilo(byte[] fotoProfilo) {
        this.fotoProfilo = fotoProfilo;
    }

    public void setPhotoType(String photoType) throws IllegalArgumentException{
        if(photoType.equals("image/png") || photoType.equals("image/jpeg")){
            this.photoType = photoType;
        }else
            throw new IllegalArgumentException("wrong MIME type"); 
    }
    
}
