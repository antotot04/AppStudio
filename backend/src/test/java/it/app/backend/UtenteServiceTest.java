package it.app.backend;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.app.backend.model.Utente; 
import it.app.backend.service.UtenteService;
import it.app.backend.repository.UtenteRepository;

// testing del service utente
public class UtenteServiceTest {

    // creo i mock per il repository e l'encoder password
    @Mock
    private UtenteRepository mockRepo;
    @Mock
    private BCryptPasswordEncoder mockEncoderPass;

    // injection nel service 
    @InjectMocks
    private UtenteService service;

    // inizializzazione mocks prima di ogni test
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    /* procedure di testing per il service */

    @Test // se utente esiste register mi deve ritornare NULL
    void test1(){
        // Utente di testing 
        Utente utente = new Utente();
        utente.setUsername("utente");
        utente.setEmail("utente@gmail.com");
        utente.setPassword("utente1234");

        // indico al repo finto di ritornare true se nel register avviene
        // il metodo indicato nella clausola when
        when(mockRepo.existsById("utente")).thenReturn(true);

        // TEST del register
        Utente registeredUtente = service.register(utente);

        // VERIFICA ESITO del test register
        assertNull(registeredUtente, "test completato. Deve restituire null");

        // verifico che il mock non abbia mai chiamato il metodo save() per ulteriore sicurezza
        verify(mockRepo, never()).save(any(Utente.class));
    }



}
