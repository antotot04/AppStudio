package it.app.backend.model;

public class UpdateRequest {

    private String email;
    private byte[] profilePhoto; 

    public String getEmail() {
        return email;
    }

    public byte[] getProfilePhoto() {
        return profilePhoto;
    }

    public void setEmail(String email) throws IllegalArgumentException{
        if(email != null && email.length() <= 320){
            this.email = email;
        }else
            throw new IllegalArgumentException("email not valid");
    }

    public void setProfilePhoto(byte[] profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}
