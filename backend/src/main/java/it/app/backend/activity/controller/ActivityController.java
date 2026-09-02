package it.app.backend.activity.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import it.app.backend.activity.model.ActivityDTO;
import it.app.backend.activity.model.ActivityData;
import it.app.backend.activity.service.ActivityService;
import it.app.backend.common.responseError;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired
    private ActivityService actService;

    @GetMapping("/user/{username}")
    public ResponseEntity<List<ActivityDTO>> getAllUserActivities(@PathVariable("username") String username){
        try {
            return ResponseEntity.ok(actService.getAllUserActivities(username));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/user/{username}")
    public ResponseEntity<responseError> registerUserActivity(@PathVariable("username") String username, @RequestBody ActivityData data){
        try {
            actService.registerActivity(username, data);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            responseError err = new responseError();
            err.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PostMapping("/user/{username}/pomo")
    public ResponseEntity<responseError> registerPomoActivity(@PathVariable("username") String username, @RequestParam("activityId") UUID activityId, @RequestParam("timestmap") Instant timestamp){
        try {
            actService.registerFullActivityPomo(username, activityId, timestamp);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            responseError err = new responseError();
            err.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }
    }

    @PutMapping("/id/{activityId}")
    public ResponseEntity<responseError> updateUserActivity(@PathVariable("activityId") UUID activityId, @RequestBody ActivityData data){
        try {
            actService.updateActivity(activityId, data);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            responseError err = new responseError();
            err.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }
    }

    @DeleteMapping("/id/{activityId}")
    public ResponseEntity<Void> deleteUserActivity(@PathVariable("activityId") UUID activityId){
        actService.deleteActivity(activityId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
