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

        when(mockService.update("utente", utente)).thenReturn(null);

        // create the currently logged-in user in the system (to later extract the username with AuthenticationPrincipal)
        Utente principal = new Utente();
        principal.setUsername("utente");

        // build the authentication token for Spring Security and put the newly created principal inside it without credentials or authorities
        var auth = new UsernamePasswordAuthenticationToken(principal, null, java.util.List.of());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/utenti/username")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objMapper.writeValueAsString(utente)))
                .andExpect(status().isNotFound());
    }

    @Test // deletion test
    void test4() throws Exception{
        // test logged-in user
        Utente principal = new Utente();
        principal.setUsername("utenteToDelete");

        // Authentication token
        var auth = new UsernamePasswordAuthenticationToken(principal, null, java.util.List.of());

        // tell deleteByUsername to throw the specified exception when called with the principal username
        doThrow(new IllegalArgumentException("username not valid"))
            .when(mockService).deleteByUsername(principal.getUsername());
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/utenti/profile")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(status().isBadRequest());
    }

}
