package it.app.backend.model;

import java.time.OffsetDateTime;


/* DTO for Registration requests */
public class RegistrationRequest {

    private String username;
    private String email;
    private String password;
    private byte[] profilePhoto; 


    public RegistrationRequest(){}

    public RegistrationRequest(String username, String email, String password, byte[] profilePhoto) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilePhoto = profilePhoto;
    }

    /* getters and setters */

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getProfilePhoto() {
        return profilePhoto;
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

    public void setProfilePhoto(byte[] profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

}
