package it.app.backend.study.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.app.backend.study.model.TrueFalseCard;

@Repository
public interface TrueFalseCardRepository extends JpaRepositoryImplementation<TrueFalseCard, UUID> {
    @Query(value="SELECT * FROM public.\"CARTA_VERO_FALSO\" AS c WHERE c.\"Id_Mazzo\" = :deckId ", nativeQuery=true)
    public List<TrueFalseCard> findByDeck(@Param("deckId") UUID deckid);
}
