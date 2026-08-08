package it.app.backend.timer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.app.backend.timer.model.SoundTrack;
import it.app.backend.timer.model.Suono;
import it.app.backend.timer.repository.SuonoRepository;

@Service
public class SuonoService {
    @Autowired
    SuonoRepository repo;

    public List<SoundTrack> getAllSounds(){
        List<SoundTrack> tracks = new ArrayList<>();
        repo.findAll().forEach((sound) -> {
            tracks.add(new SoundTrack(
                sound.getId_Suono(), 
                sound.getTipo(), 
                sound.getNome()
            ));
        });
        return tracks;
    }

    public Suono getSound(UUID trackId) throws IllegalArgumentException{
        Optional<Suono> soundOpt = repo.findById(trackId);
        if(soundOpt.isEmpty()){
            throw new IllegalArgumentException("invalid id");
        }
        return soundOpt.get();
    }
}
