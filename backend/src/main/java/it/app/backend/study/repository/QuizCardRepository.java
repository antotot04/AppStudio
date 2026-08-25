package it.app.backend.study.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.app.backend.study.model.QuizCard;

@Repository
public interface QuizCardRepository extends JpaRepository<QuizCard, UUID> {
    @Query(value="SELECT * FROM public.\"CARTA_RISP_MULTIPLA\" AS c WHERE c.\"Id_Mazzo\" = :deckId ", nativeQuery=true)
    public List<QuizCard> findByDeck(@Param("deckId") UUID deckid);
}
