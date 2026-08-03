package it.app.backend.model;

public class UtenteResponse {
    private String email; 
    private byte[] profilePhoto;
    private String photoType;

    public UtenteResponse(String email, byte[] profilePhoto, String photoType) {
        this.email = email;
        this.profilePhoto = profilePhoto;
        this.photoType = photoType;
    }

    public String getEmail() {
        return email;
    }

    public byte[] getProfilePhoto() {
        return profilePhoto;
    }

    public String getPhotoType() {
        return photoType;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfilePhoto(byte[] profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public void setPhotoType(String photoType) {
        this.photoType = photoType;
    }

    
}
