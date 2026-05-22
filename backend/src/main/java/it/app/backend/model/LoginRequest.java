package it.app.backend.model;


/* DTO per richieste di login */
public class LoginRequest {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) throws IllegalArgumentException{
        if(username != null && username.length() <= 30){
            this.username = username;
        }else
            throw new IllegalArgumentException("username non valido");
        
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws IllegalArgumentException{
        if(password != null && password.length() <= 60){
            this.password = password;
        }else
            throw new IllegalArgumentException("password non valida");
    }

    
}
