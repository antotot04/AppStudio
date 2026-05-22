package it.app.backend;

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
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import it.app.backend.model.Utente;
import it.app.backend.repository.UtenteRepository;
import it.app.backend.service.UtenteService;

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

    /* procedure di testing per il register */

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
        assertNull(registeredUtente, "Deve restituire null");

        // verifico che il mock non abbia mai chiamato il metodo save() per ulteriore sicurezza
        verify(mockRepo, never()).save(any(Utente.class));
    }

    @Test // la funzione register deve ritornare una IllegalArgumentException quando la password non è valida
    void test2(){
        // Utente di testing con password troppo corta e senza numeri 
        Utente utente = new Utente();
        utente.setUsername("utente");
        utente.setEmail("utente@gmail.com");
        utente.setPassword("ute"); 

        // Test e verifica che sia una IllegalArgumentEzception
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> service.register(utente), "deve lanciare una IllegalArgumentException");
        // verifica che il messaggio dell'eccezione coincida con quello desiderato
        assertEquals("La password deve contenere più di 6 caratteri e almeno un numero", e.getMessage());
    }

    @Test // procedura di registrazione avvenuta con successo
    void test3(){
        // Utente di testing
        Utente utente = new Utente();
        utente.setUsername("utente");
        utente.setEmail("utente@gmail.com");
        utente.setPassword("utente1234");

        // test 
        when(mockRepo.existsById("utente")).thenReturn(false);
        when(mockEncoderPass.encode(utente.getPassword())).thenReturn("HASH");
        when(mockRepo.save(any(Utente.class))).thenReturn(utente);

        Utente risultato = service.register(utente);

        // verifiche sul test appena fatto
        assertNotNull(risultato);
        assertEquals("HASH", risultato.getPassword());
        assertNotNull(risultato.getDataCreazione()); // data di creazione assegnata nel register se tutto andato a buon fine

        verify(mockRepo, times(1)).save(any(Utente.class));
        verify(mockEncoderPass, times(1)).encode("utente1234");

    }

}
