package it.app.backend.activity.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.app.backend.activity.model.ActivityPomo;

@Repository
public interface ActivityPomoRepository extends JpaRepository<ActivityPomo, UUID> {
    @Query(value="SELECT * FROM public.\"POM_ASSOCIATO\" as p WHERE p.\"Id_Attività\" = :actId ", nativeQuery=true)
    public List<ActivityPomo> getCurrPomoCount(@Param("actId") UUID activityId);
}
