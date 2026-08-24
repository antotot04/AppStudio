package it.app.backend.timer.controller;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.app.backend.common.responseError;
import it.app.backend.timer.model.BackgroundInfo;
import it.app.backend.timer.model.LeaderboardUser;
import it.app.backend.timer.model.SelectBackground;
import it.app.backend.timer.model.Settings;
import it.app.backend.timer.model.SettingsDTO;
import it.app.backend.timer.model.SoundTrack;
import it.app.backend.timer.model.Suono;
import it.app.backend.timer.model.SuonoDTO;
import it.app.backend.timer.model.TimerDTO;
import it.app.backend.timer.model.UserSettings;
import it.app.backend.timer.service.PomodoroService;
import it.app.backend.timer.service.SelectBackgroundService;
import it.app.backend.timer.service.SettingsService;
import it.app.backend.timer.service.SuonoService;

@RestController
@RequestMapping("/api/timer")
public class TimerController {
    @Autowired
    private PomodoroService pomoService;
    @Autowired
    private SuonoService soundService;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private SelectBackgroundService selectBackgroundService;

    @GetMapping(path="/sounds")
    public ResponseEntity<List<SoundTrack>> getSoundTracks(){
        return ResponseEntity.ok().body(soundService.getAllSounds());
    }

    @GetMapping(path="/leaderboard")
    public ResponseEntity<List<LeaderboardUser>> getLeaderboard(@RequestParam int userQuantity, @RequestParam String timeSpan){
        return ResponseEntity.ok().body(pomoService.getLeaderboard(userQuantity, timeSpan));
    }

    @GetMapping(path="/{username}/leaderboard")
    public ResponseEntity<LeaderboardUser> getUserLeaderboard(@PathVariable String username, @RequestParam String timeSpan, @RequestParam int relativeQuantity){
        LeaderboardUser userTarget = pomoService.getUserLeaderboard(username, timeSpan, relativeQuantity);
        if(userTarget != null){
            return ResponseEntity.ok().body(userTarget);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path="/sound/{soundId}")
    public ResponseEntity<byte[]> getSound(@PathVariable("soundId") UUID soundId){
        try {
            Optional<Suono> requestedSoundOpt = soundService.getSound(soundId);
            if(requestedSoundOpt.isEmpty()){
                throw new IllegalArgumentException("invalid id");
            }
            Suono requestedSound = requestedSoundOpt.get();

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(requestedSound.getMIME_Type()))
                .body(requestedSound.getContenuto());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path="/{username}/settings")
    public ResponseEntity<UserSettings> getUserSettings(@PathVariable("username") String username){
        try{
            Optional<Settings> userSettingsOpt = settingsService.getUserSettings(username);
            Optional<SelectBackground> userBackgroundOpt = selectBackgroundService.getBackgroundSettings(username);
            if(userSettingsOpt.isEmpty()){
                return ResponseEntity.notFound().build();
            }else{
                Settings userSettings = userSettingsOpt.get();
                SelectBackground userBackground = userBackgroundOpt.orElse(null);

                if(userBackground != null){
                    return ResponseEntity.ok().body(new UserSettings(
                        new TimerDTO(
                            settingsService.toSeconds(userSettings.getShortPause()),
                            settingsService.toSeconds(userSettings.getLongPause()),
                            userSettings.getLongFreq()
                        ),
                        new SuonoDTO(
                            userSettings.getRingtone().getId_Suono(), 
                            userSettings.getRingtoneVolume(),
                            userBackground.getBackgroundSound().getId_Suono(),
                            userBackground.getBackgroundVolume())
                    ));
                }else{
                    return ResponseEntity.ok().body(new UserSettings(
                        new TimerDTO(
                            settingsService.toSeconds(userSettings.getShortPause()),
                            settingsService.toSeconds(userSettings.getLongPause()),
                            userSettings.getLongFreq()
                        ),
                        new SuonoDTO(
                            userSettings.getRingtone().getId_Suono(), 
                            userSettings.getRingtoneVolume()
                        )
                    ));
                }
            }
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(path="/{username}/background")
    public ResponseEntity<responseError> registerBackground(@PathVariable("username") String username, @RequestBody BackgroundInfo backgroundSettings){
        try {
            selectBackgroundService.registerBackground(username, backgroundSettings);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
        }
    }

    @PostMapping(path="/{username}/pomodoro")
    public ResponseEntity<responseError> registerPomodoro(@PathVariable("username") String username, @RequestParam Instant timestamp){
        try{
            pomoService.registerUserPomo(username, timestamp);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch(Exception e){
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
        }
    }

    @PutMapping(path="/{username}/background")
    public ResponseEntity<responseError> updateBackground(@PathVariable("username") String username, @RequestBody BackgroundInfo background){
        try {
            selectBackgroundService.UpdateBakcground(username, background);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
        }
    }

    @PutMapping(path="/{username}/settings")
    public ResponseEntity<responseError> updateSettings(@PathVariable("username") String username, @RequestBody SettingsDTO background){
        try {
            settingsService.updateUserSettings(username, background);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
        }
    }

    @DeleteMapping(path="/{username}/background")
    public ResponseEntity<responseError> deleteBackground(@PathVariable("username") String username){
        try {
            selectBackgroundService.deleteBackground(username);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }
}