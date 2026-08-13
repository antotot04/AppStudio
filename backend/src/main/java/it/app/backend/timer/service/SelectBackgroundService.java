package it.app.backend.timer.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.app.backend.timer.model.BackgroundInfo;
import it.app.backend.timer.model.SelectBackground;
import it.app.backend.timer.model.Settings;
import it.app.backend.timer.model.Suono;
import it.app.backend.timer.repository.SelectBackgroundRepository;
import it.app.backend.timer.repository.SettingsRepository;

@Service
public class SelectBackgroundService {
    @Autowired
    private SelectBackgroundRepository backgroundRepo;
    @Autowired
    private SuonoService suonoService;
    @Autowired
    private SettingsRepository settingsRepo;

    public Optional<SelectBackground> getBackgroundSettings(String username){
        return backgroundRepo.findById(username);
    }

    public void registerBackground(String username, BackgroundInfo background) throws IllegalArgumentException{
        if(username == null || background == null){
            throw new IllegalArgumentException("null background or username");
        }

        if(backgroundRepo.existsById(username)){
            throw new IllegalArgumentException("background already exists");
        }

        /* settings check */
        Optional<Settings> userSet = settingsRepo.findById(username);
        if(userSet.isEmpty()){
            throw new IllegalArgumentException("requested settings does not exists");
        }

        /* background sound checks */
        Optional<Suono> backgroundSound = suonoService.getSound(background.getSound());
        if(backgroundSound.isEmpty()){
            throw new IllegalArgumentException("invalid background sound id");
        }

        if(!backgroundSound.get().getTipo().equals("background")){
            throw new IllegalArgumentException("new sound not a background");
        }

        if(background.getVolume() < 0 || background.getVolume() > 100){
            throw new IllegalArgumentException("invalid background suond volume");
        }

        SelectBackground backgroundToSave = new SelectBackground();
        backgroundToSave.setSettings(userSet.get());
        backgroundToSave.setBackgroundSound(backgroundSound.get());
        backgroundToSave.setBackgroundVolume(background.getVolume());
        backgroundToSave.setIsActive(true);

        backgroundRepo.save(backgroundToSave);
    }

    public void UpdateBakcground(String username, BackgroundInfo backgroundToUpdate){
        if(username == null || backgroundToUpdate == null){
            throw new IllegalArgumentException("null background or username");
        }
        /* entity checks */
        SelectBackground currentBackground = getBackgroundSettings(username).orElse(null);
        if(currentBackground == null){
            throw new IllegalArgumentException("background settings not found");
        }

        /* background sound checks */
        Optional<Suono> backgroundSound = suonoService.getSound(backgroundToUpdate.getSound());
        if(backgroundSound.isEmpty()){
            throw new IllegalArgumentException("invalid background sound id");
        }

        if(!backgroundSound.get().getTipo().equals("background")){
            throw new IllegalArgumentException("new sound not a background");
        }

        int volume = backgroundToUpdate.getVolume();
        if(volume < 0 || volume > 100){
            throw new IllegalArgumentException("invalid background suond volume");
        }

        currentBackground.setBackgroundSound(backgroundSound.get());
        currentBackground.setBackgroundVolume(volume);
        backgroundRepo.save(currentBackground);
    }

    public void deleteBackground(String username) throws IllegalArgumentException{
        backgroundRepo.deleteById(username);
    }
}
