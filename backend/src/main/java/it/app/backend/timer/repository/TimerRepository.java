package it.app.backend.timer.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.app.backend.timer.model.Pomodoro;

@Repository
public interface TimerRepository extends JpaRepository<Pomodoro, UUID> {

    public List<Pomodoro> findByUtenteUsername(String username);
}
