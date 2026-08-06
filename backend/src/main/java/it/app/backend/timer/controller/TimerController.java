package it.app.backend.timer.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.app.backend.common.responseError;
import it.app.backend.timer.model.Pomodoro;
import it.app.backend.timer.service.TimerService;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/api/timer")
public class TimerController {
    @Autowired
    private TimerService service;

    @GetMapping(path="/{username}/pomodoros")
    public ResponseEntity<List<Pomodoro>> getAllPomodoros(@PathParam("username") String username){
        return ResponseEntity.status(HttpStatus.OK).body(service.findUserPomos(username));
    }

    @PostMapping(path="/{username}/pomodoro")
    public ResponseEntity<responseError> registerPomodoro(@PathParam("username") String username, @RequestParam Instant timestamp){
        try{
            service.registerUserPomo(username, timestamp);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch(IllegalArgumentException e){
            responseError resp = new responseError();
            resp.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
    }
}
