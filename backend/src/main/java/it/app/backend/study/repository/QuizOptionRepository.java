package it.app.backend.study.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.app.backend.study.model.OptionId;
import it.app.backend.study.model.QuizOption;

@Repository
public interface QuizOptionRepository extends JpaRepository<QuizOption, OptionId> {
    @Query(value="SELECT * FROM public.\"OPZIONI_RISP_MULTIPLA\" AS o WHERE o.\"Id_Carta\" = :cardId ", nativeQuery=true)
    public List<QuizOption> findByCard(@Param("cardId") UUID cardId);
}
