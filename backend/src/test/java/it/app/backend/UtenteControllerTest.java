package it.app.backend;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.app.backend.service.UtenteService;
import it.app.backend.model.LoginRequest;
import it.app.backend.model.Utente;

@SpringBootTest
@AutoConfigureMockMvc // mock per simulare chiamate http
public class UtenteControllerTest {
    
    @Autowired
    private MockMvc mockMvc; 

    @Autowired
    private ObjectMapper objMapper; // per conversione: JSON <--> oggetto Java

    @MockitoBean
    private UtenteService mockService;

    @Test // procedura di login in caso di credenziali sbagliate 
    void test1() throws Exception{
        // oggetto di testing
        LoginRequest usrNoPassSi = new LoginRequest();
        usrNoPassSi.setUsername("utenteSbagliato");
        usrNoPassSi.setPassword("passwordCorretta123");

        // ordino al service finto di rispondere falso al tentativo di login
        when(mockService.verifyLogin("utenteSbagliato", "passwordCorretta123")).thenReturn(false);

        // simulo una richiesta in post sull'api di login e testo il risultato
        mockMvc.perform(MockMvcRequestBuilders.post("/api/utenti/login")
                .contentType(MediaType.APPLICATION_JSON) 
                .content(objMapper.writeValueAsString(usrNoPassSi))) // Trasforma l'oggetto in JSON
                .andExpect(status().isUnauthorized()); // status atteso 
    }

    @Test // procedura di registrazione se utente esiste già
    void test2() throws Exception{
        // utente di testing
        Utente utente = new Utente(); 
        utente.setUsername("utente");
        utente.setEmail("utente@gmail.com");
        utente.setPassword("utente1234");

        when(mockService.register(utente)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/utenti/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objMapper.writeValueAsString(utente)))
                .andExpect(status().isConflict());
    }

    @Test // caso utente non trovato nel db 
    void test3() throws Exception {
        // utente di testing
        Utente utente = new Utente();
        utente.setUsername("utente");
        utente.setEmail("utente@gmail.com");
        utente.setPassword("utente1234");

        when(mockService.update("utente", utente)).thenReturn(null);

        // creo l'utente attualmente loggato nel sistema (per estrarre il suo username successivamente con AuthenticationPrincipal)
        Utente principal = new Utente();
        principal.setUsername("utente");

        // costruisco l'Autentication token per spring security metto dentro il principal appena creato senza credenziali e autorità
        var auth = new UsernamePasswordAuthenticationToken(principal, null, java.util.List.of());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/utenti/username")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objMapper.writeValueAsString(utente)))
                .andExpect(status().isNotFound());
    }

    @Test // test eliminazione cannata
    void test4() throws Exception{
        // utente loggato di test
        Utente principal = new Utente();
        principal.setUsername("utenteToDelete");

        // Autentication token
        var auth = new UsernamePasswordAuthenticationToken(principal, null, java.util.List.of());

        // ordino a delteByUsername se chiamata con lo username del principal di lanciare l'eccezzione indicata
        doThrow(new IllegalArgumentException("username non valido"))
            .when(mockService).deleteByUsername(principal.getUsername());
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/utenti/profilo")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(status().isBadRequest());
    }

}
