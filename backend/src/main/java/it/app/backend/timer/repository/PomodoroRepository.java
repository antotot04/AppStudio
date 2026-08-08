package it.app.backend.timer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.app.backend.timer.model.Pomodoro;

@Repository
public interface PomodoroRepository extends JpaRepository<Pomodoro, UUID> {

}
