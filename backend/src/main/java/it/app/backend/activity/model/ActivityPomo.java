package it.app.backend.activity.model;

import java.util.UUID;

import it.app.backend.timer.model.Pomodoro;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="POM_ASSOCIATO", schema="public")
public class ActivityPomo {
    @Id
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name="Id_Pomodoro", referencedColumnName="Id_Pomodoro", nullable=false)
    private Pomodoro pomodoro;

    @ManyToOne
    @JoinColumn(name="Id_Attività", referencedColumnName="Id_Attività", nullable=false)
    private Activity activity;

    public UUID getId() {
        return id;
    }

    public Pomodoro getPomodoro() {
        return pomodoro;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setPomodoro(Pomodoro pomodoro) throws IllegalArgumentException {
        if(pomodoro == null){
            throw new IllegalArgumentException("pomodoro can't be null");
        }
        this.pomodoro = pomodoro;
    }

    public void setActivity(Activity activity) throws IllegalArgumentException {
        if(activity == null){
            throw new IllegalArgumentException("activity can't be null");
        }
        this.activity = activity;
    }
}
