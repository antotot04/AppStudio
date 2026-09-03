package it.app.backend.activity.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.app.backend.activity.model.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    @Query(value="SELECT * FROM public.\"ATTIVITA\" as a WHERE a.\"Username\" = :thisUsername ", nativeQuery=true)
    public List<Activity> findAllByUsername(@Param("thisUsername") String username);

    @Modifying
    @Transactional
    @Query(value="DELETE FROM public.\"ATTIVITA\" as a WHERE a.\"Username\" = :thisUsername AND a.\"Pomodori_Da_Completare\" = ( SELECT COUNT(*) FROM public.\"POM_ASSOCIATO\" as p WHERE p.\"Id_Attività\" = a.\"Id_Attività\" ) ", nativeQuery=true)
    public void deleteCompletedByUsername(@Param("thisUsername") String username);
}
