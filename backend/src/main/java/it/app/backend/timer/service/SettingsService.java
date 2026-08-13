package it.app.backend.timer.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.app.backend.timer.model.Settings;
import it.app.backend.timer.model.SettingsDTO;
import it.app.backend.timer.model.Suono;
import it.app.backend.timer.repository.SettingsRepository;
import it.app.backend.utente.model.Utente;
import it.app.backend.utente.repository.UtenteRepository;

@Service
public class SettingsService {
    @Autowired
    private SettingsRepository settingsRepo;
    @Autowired
    private UtenteRepository utenteRepo;
    @Autowired
    private SuonoService suonoService;

    /* interval (hh:mm:ss) <-> seconds */
    public int toSeconds(String interval){
        int hours = Integer.parseInt(interval.substring(0, 2));
        int minutes = Integer.parseInt(interval.substring(3, 5));
        int seconds = Integer.parseInt(interval.substring(6));
        return seconds + minutes * 60 + hours * 3600;
    }

    public String toInterval(int rawSeconds){
        int hours = rawSeconds / 3600;
        int remainingSeconds = rawSeconds % 3600; 
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;

        String formattedHours = hours + "";
        String formattedMinutes = minutes + "";
        String formattedSeconds = seconds + "";

        if(hours < 10){
            formattedHours = "0" + hours;
        }

        if(minutes < 10){
            formattedMinutes = "0" + minutes;
        }

        if(seconds < 10){
            formattedSeconds = "0" + seconds;
        }

        return formattedHours + ":" + formattedMinutes + ":" + formattedSeconds;
    }

    public Optional<Settings> getUserSettings(String username){
        return settingsRepo.findById(username);
    }


    /* here I'm registering the default user settings. This registration happens as soon as a new user (Utente) signs up */
    public void registerUserSettings(String username, SettingsDTO settings) throws IllegalArgumentException{

        if(settings == null || username == null){
            throw new IllegalArgumentException("null settings or username");
        }

        /* user checks */
        Optional<Utente> utente = utenteRepo.findById(username);
        if(utente.isEmpty()){
            throw new IllegalArgumentException("utente does not exists");
        }

        /* settings check */
        if(settingsRepo.existsById(username)){
            throw new IllegalArgumentException(username + " settings already created");
        }

        /* ringtone checks */
        Optional<Suono> ringtone = suonoService.getSound(settings.getRingtone());
        if(ringtone.isEmpty()){
            throw new IllegalArgumentException("invalid ringtone id");
        }

        if(!ringtone.get().getTipo().equals("ringtone")){
            throw new IllegalArgumentException("new sound is not a ringtone");
        }

        if(settings.getRingtoneVolume() < 0 || settings.getRingtoneVolume() > 100){
            throw new IllegalArgumentException("invalid ringtone volume");
        }

        if(settings.getLongFreq() < 0 && settings.getLongPause() < 0 && settings.getShortPause() < 0){
            throw new IllegalArgumentException("time and frequency cannot be negative");
        }

        // registering user settings
        Settings userSet = new Settings();
        userSet.setUtente(utente.get());
        userSet.setShortPause(toInterval(settings.getShortPause()));
        userSet.setLongPause(toInterval(settings.getLongPause()));
        userSet.setLongFreq(settings.getLongFreq());
        userSet.setRingtone(ringtone.get());
        userSet.setRingtoneVolume(settings.getRingtoneVolume());
        settingsRepo.save(userSet);
    }

    public void updateUserSettings(String username, SettingsDTO settingsToUpdate){
        if(settingsToUpdate == null || username == null){
            throw new IllegalArgumentException("null settings or username");
        }

        /* user checks */
        if(!utenteRepo.existsById(username)){
            throw new IllegalArgumentException("utente does not exists");
        }

        /* settings entity check */
        Settings currentSettings = getUserSettings(username).orElse(null);
        if(currentSettings == null){
            throw new IllegalArgumentException("user settings not found");
        }

        /* ringtone checks */
        Optional<Suono> ringtone = suonoService.getSound(settingsToUpdate.getRingtone());
        if(ringtone.isEmpty()){
            throw new IllegalArgumentException("invalid ringtone id");
        }

        if(!ringtone.get().getTipo().equals("ringtone")){
            throw new IllegalArgumentException("new sound is not a ringtone");
        }

        if(settingsToUpdate.getRingtoneVolume() < 0 || settingsToUpdate.getRingtoneVolume() > 100){
            throw new IllegalArgumentException("invalid ringtone volume");
        }

        if(settingsToUpdate.getLongFreq() < 0 && settingsToUpdate.getLongPause() < 0 && settingsToUpdate.getShortPause() < 0){
            throw new IllegalArgumentException("time and frequency cannot be negative");
        }

        currentSettings.setShortPause(toInterval(settingsToUpdate.getShortPause()));
        currentSettings.setLongPause(toInterval(settingsToUpdate.getLongPause()));
        currentSettings.setLongFreq(settingsToUpdate.getLongFreq());
        currentSettings.setRingtone(ringtone.get());
        currentSettings.setRingtoneVolume(settingsToUpdate.getRingtoneVolume());

        settingsRepo.save(currentSettings);
    }
}
