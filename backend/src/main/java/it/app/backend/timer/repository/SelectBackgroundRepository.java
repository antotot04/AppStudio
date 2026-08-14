package it.app.backend.timer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.app.backend.timer.model.SelectBackground;

@Repository
public interface SelectBackgroundRepository extends JpaRepository<SelectBackground, String> {

}
