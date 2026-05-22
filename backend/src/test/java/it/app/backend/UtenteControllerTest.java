package it.app.backend;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.app.backend.service.UtenteService;
import it.app.backend.model.LoginRequest;

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



}
