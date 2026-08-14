package it.app.backend;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import it.app.backend.utente.model.RegistrationRequest;
import it.app.backend.utente.model.Utente;
import it.app.backend.utente.repository.UtenteRepository;
import it.app.backend.utente.service.UtenteService;

// testing of the user service
public class UtenteServiceTest {

    // create mocks for the repository and password encoder
    @Mock
    private UtenteRepository mockRepo;
    @Mock
    private BCryptPasswordEncoder mockEncoderPass;

    // inject into the service
    @InjectMocks
    private UtenteService service;

    // reset mocks before each test
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    /* testing procedures for register */

    @Test // if the user exists, register should return NULL
    void test1(){
        // test user
        RegistrationRequest utente = new RegistrationRequest();
        utente.setUsername("utente");
        utente.setEmail("utente@gmail.com");
        utente.setPassword("utente1234");

        // tell the mocked repo to return true for the specified method call in the register flow
        when(mockRepo.existsById("utente")).thenReturn(true);

        // register test
        Utente registeredUtente = service.register(utente);

        // verify register test outcome
        assertNull(registeredUtente, "It should return null");

        // verify that the mock never called save() for extra safety
        verify(mockRepo, never()).save(any(Utente.class));
    }

    @Test // the register function should throw an IllegalArgumentException when the password is invalid
    void test2(){
        // test user with a too-short password and without numbers
        RegistrationRequest utente = new RegistrationRequest();
        utente.setUsername("utente");
        utente.setEmail("utente@gmail.com");
        utente.setPassword("ute"); 

        // test and verify that it is an IllegalArgumentException
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> service.register(utente), "it should throw an IllegalArgumentException");
        // verify that the exception message matches the expected one
        assertEquals("The password must contain more than 6 characters and at least one number", e.getMessage());
    }

    @Test // successful registration flow
    void test3(){
        // test user
        RegistrationRequest utente = new RegistrationRequest();
        utente.setUsername("utente");
        utente.setEmail("utente@gmail.com");
        utente.setPassword("utente1234");

        Utente returnedUtente = new Utente();
        returnedUtente.setPassword("HASH");
        returnedUtente.setDataCreazione(OffsetDateTime.now());

        // test
        when(mockRepo.existsById("utente")).thenReturn(false);
        when(mockEncoderPass.encode(utente.getPassword())).thenReturn("HASH");
        when(mockRepo.save(any(Utente.class))).thenReturn(returnedUtente);

        Utente result = service.register(utente);

        // checks on the test just performed
        assertNotNull(result);
        assertEquals("HASH", result.getPassword());
        assertNotNull(result.getDataCreazione()); // creation date assigned in register if everything went well

        verify(mockRepo, times(1)).save(any(Utente.class));
        verify(mockEncoderPass, times(1)).encode("utente1234");

    }

    @Test // update flow but user not found
    void test4(){
        // test user
        Utente utente = new Utente();
        utente.setUsername("utente");
        utente.setEmail("utente@gmail.com");
        utente.setPassword("newutente1234"); // new password

        // test
        when(mockRepo.findById(utente.getUsername())).thenReturn(Optional.empty()); // findById returns an Optional

        Utente risultato = service.updatePassword(utente.getUsername(), utente.getPassword());

        assertNull(risultato, "this result should be null");

        verify(mockRepo, times(1)).findById(utente.getUsername());
        
    }

    @Test // login flow with the correct username but wrong password
    void test5(){
        // test credentials
        String username = "user1234";
        String wrongPassword = "qwerty1234";

        // test user
        Utente utente = new Utente();
        utente.setUsername("user1234");
        utente.setEmail("utente@gmail.com");
        utente.setPassword("HASH");

        // test
        when(mockRepo.findById(username)).thenReturn(Optional.of(utente));
        when(mockEncoderPass.matches(wrongPassword, utente.getPassword())).thenReturn(false);

        var risultato = service.verifyLogin(username, wrongPassword);

        assertEquals(false, risultato);

        verify(mockRepo, times(1)).findById(username);
        verify(mockEncoderPass, times(1)).matches(wrongPassword, utente.getPassword());
    }

}
