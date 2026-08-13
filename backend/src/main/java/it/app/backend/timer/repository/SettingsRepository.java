package it.app.backend.timer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.app.backend.timer.model.Settings;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, String> {

}
