package it.app.backend.timer.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import it.app.backend.common.responseError;
import it.app.backend.timer.model.SoundTrack;
import it.app.backend.timer.model.Suono;
import it.app.backend.timer.service.PomodoroService;
import it.app.backend.timer.service.SuonoService;

@RestController
@RequestMapping("/api/timer")
public class TimerController {
    @Autowired
    private PomodoroService pomoService;
    @Autowired
    private SuonoService soundService;

    @GetMapping(path="/sounds")
    public ResponseEntity<List<SoundTrack>> getSoundTracks(){
        return ResponseEntity.ok().body(soundService.getAllSounds());
    }

    @GetMapping(path="/sound/{soundId}")
    public ResponseEntity<byte[]> getSound(@PathVariable("soundId") UUID soundId){
        try {
            Suono requestedSound = soundService.getSound(soundId);
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(requestedSound.getMIME_Type()))
                .body(requestedSound.getContenuto());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }
}
