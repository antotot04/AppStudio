package it.app.backend.activity.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.app.backend.activity.model.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    @Query(value="SELECT * FROM public.\"ATTIVITA\" as a WHERE a.\"Username\" = :thisUsername ", nativeQuery=true)
    public List<Activity> findAllByUsername(@Param("thisUsername") String username);
}
