package it.app.backend.utente.model;

public class UtenteResponse {
    private String email;
    private Boolean hasPhoto;

    public UtenteResponse(String email, Boolean hasPhoto) {
        this.email = email;
        this.hasPhoto = hasPhoto;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getHasPhoto() {
        return hasPhoto;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHasPhoto(Boolean hasPhoto) {
        this.hasPhoto = hasPhoto;
    }
    
}
