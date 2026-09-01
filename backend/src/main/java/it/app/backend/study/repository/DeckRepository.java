package it.app.backend.study.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.app.backend.study.model.Deck;
import it.app.backend.study.model.DeckDTO;

@Repository
public interface DeckRepository extends JpaRepository<Deck, UUID> {
    @Query(value="SELECT m.\"Id_Mazzo\", m.\"Nome\", m.\"Layout\" FROM public.\"MAZZO\" AS m WHERE m.\"Username\" = :user ", nativeQuery=true)
    public List<DeckDTO> findByUtente(@Param("user") String Username);
}
