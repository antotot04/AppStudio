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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.app.backend.service.UtenteService;
import it.app.backend.model.LoginRequest;
import it.app.backend.model.Utente;

@SpringBootTest
@AutoConfigureMockMvc // mock to simulate HTTP calls
public class UtenteControllerTest {
    
    @Autowired
    private MockMvc mockMvc; 

    @Autowired
    private ObjectMapper objMapper; // for JSON <--> Java object conversion

    @MockitoBean
    private UtenteService mockService;

    @Test // login flow with wrong credentials
    void test1() throws Exception{
        // test object
        LoginRequest usrNoPassSi = new LoginRequest();
        usrNoPassSi.setUsername("wrongUser");
        usrNoPassSi.setPassword("correctPassword123");

        // tell the mocked service to return false for the login attempt
        when(mockService.verifyLogin("wrongUser", "correctPassword123")).thenReturn(false);

        // simulate a POST request to the login API and verify the result
        mockMvc.perform(MockMvcRequestBuilders.post("/api/utenti/login")
                .contentType(MediaType.APPLICATION_JSON) 
                .content(objMapper.writeValueAsString(usrNoPassSi))) // Transform the object into JSON
                .andExpect(status().isUnauthorized()); // expected status
    }

    @Test // registration flow when the user already exists
    void test2() throws Exception{
        // test user
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

    @Test // user not found in the DB
    void test3() throws Exception {
        // test user
        Utente utente = new Utente();
        utente.setUsername("utente");
        utente.setEmail("utente@gmail.com");
        utente.setPassword("utente1234");

        when(mockService.update(eq("wrongUtente"), any(Utente.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/utenti/wrongUtente")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objMapper.writeValueAsString(utente)))
                .andExpect(status().isNotFound());
    }

    @Test // deletion test
    void test4() throws Exception{
        // tell deleteByUsername to throw the specified exception when called with this username
        doThrow(new IllegalArgumentException("username not valid"))
            .when(mockService).deleteByUsername(eq("userToDelete"));
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/utenti/userToDelete"))
                .andExpect(status().isBadRequest());
    }

}
