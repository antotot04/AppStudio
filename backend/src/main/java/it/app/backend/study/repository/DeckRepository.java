package it.app.backend.study.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.app.backend.study.model.Deck;
import it.app.backend.study.model.DeckDTO;

public interface DeckRepository extends JpaRepository<Deck, UUID> {
    @Query(value="SELECT m.\"Id_Mazzo\", m.\"Nome\", m.\"Layout\" FROM public.\"MAZZO\" AS m JOIN public.\"UTENTE\" AS u ON m.\"Username\" = u.\"Username\" ", nativeQuery=true)
    public List<DeckDTO> findByUtente(String Username);
}
