package it.app.backend.timer.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.app.backend.common.responseError;
import it.app.backend.timer.service.TimerService;

@RestController
@RequestMapping("/api/timer")
public class TimerController {
    @Autowired
    private TimerService service;

    @PostMapping(path="/{username}/pomodoro")
    public ResponseEntity<responseError> registerPomodoro(@PathVariable("username") String username, @RequestParam Instant timestamp){
        try{
            service.registerUserPomo(username, timestamp);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch(Exception e){
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }
}
