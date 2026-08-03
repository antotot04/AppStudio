package it.app.backend.model;

public class UpdateRequest {

    private String email;
    private byte[] profilePhoto; 
    private String photoType;

    public UpdateRequest() {}

    public UpdateRequest(String email, byte[] profilePhoto, String photoType) {
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

    public void setEmail(String email) throws IllegalArgumentException{
        if(email != null && email.length() <= 320){
            this.email = email;
        }else
            throw new IllegalArgumentException("email not valid");
    }

    public void setProfilePhoto(byte[] profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public void setPhotoType(String photoType) throws IllegalArgumentException{
        if(photoType.equals("image/png") || photoType.equals("image/jpeg")){
            this.photoType = photoType;
        }else
            throw new IllegalArgumentException("wrong MIME type"); 
    }
}
