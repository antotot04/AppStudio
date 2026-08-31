package it.app.backend.study.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.app.backend.study.model.DoubleSidedCard;

@Repository
public interface DoubleSidedCardRepository extends JpaRepository<DoubleSidedCard, UUID> {
    @Query(value="SELECT * FROM public.\"CARTA_FRONTE_RETRO\" AS c WHERE c.\"Id_Mazzo\" = :deckId ", nativeQuery=true)
    public List<DoubleSidedCard> findByDeck(@Param("deckId") UUID deckid);
}
