package it.app.backend.timer.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.app.backend.timer.model.LeaderboardUser;
import it.app.backend.timer.model.Pomodoro;

@Repository
public interface PomodoroRepository extends JpaRepository<Pomodoro, UUID> {
    @Query(value= "SELECT DENSE_RANK() OVER (ORDER BY p.\"Username\" ASC) AS \"rank\", p.\"Username\" AS \"username\", COUNT(*) AS \"pomodoroCounter\" FROM public.\"POMODORO\" AS p WHERE p.\"Timestamp\" >= NOW() - :time ::interval GROUP BY p.\"Username\" ORDER BY COUNT(*) DESC LIMIT :limit", nativeQuery=true)
    List<LeaderboardUser> getLeaderboard(@Param("time") String time, @Param("limit") int limit);

    @Query(value="SELECT \"rank\", \"username\", \"pomodoroCounter\" FROM (SELECT DENSE_RANK() OVER ( ORDER BY p.\"Username\" ASC) AS \"rank\", p.\"Username\" AS \"username\", COUNT(*) AS \"pomodoroCounter\" FROM public.\"POMODORO\" AS p WHERE p.\"Timestamp\" >= NOW() - :time ::interval GROUP BY p.\"Username\" ORDER BY COUNT(*) DESC LIMIT :limit ) WHERE \"username\" = :username", nativeQuery=true)
    LeaderboardUser getUserLeaderboard(@Param("time") String time, @Param("username") String username, @Param("limit") int limit);
}
