package it.app.backend.timer.model;

public class LeaderboardUser {
    private long rank;
    private String username;
    private long pomodoroCounter;

    public LeaderboardUser() {
    }

    public LeaderboardUser(long rank, String username, long pomodoroCounter) {
        this.rank = rank;
        this.username = username;
        this.pomodoroCounter = pomodoroCounter;
    }

    public long getPomodoroCounter() {
        return pomodoroCounter;
    }

    public long getRank() {
        return rank;
    }

    public String getUsername() {
        return username;
    }

    public void setPomodoroCounter(long pomodoroCounter) {
        this.pomodoroCounter = pomodoroCounter;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
