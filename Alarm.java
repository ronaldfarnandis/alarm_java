import java.time.LocalTime;
import java.io.Serializable;

public class Alarm implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private LocalTime time;
    private String musicFilePath;
    private boolean enabled;

    public Alarm(LocalTime time, String musicFilePath) {
        this.time = time.withSecond(0).withNano(0); // Sync to the minute mark
        this.musicFilePath = musicFilePath;
        this.enabled = true;
    }

    // Getters and Setters
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getMusicFilePath() { return musicFilePath; }
    public void setMusicFilePath(String musicFilePath) { this.musicFilePath = musicFilePath; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @Override
    public String toString() {
        return String.format("%02d:%02d (%s)", time.getHour(), time.getMinute(), 
                musicFilePath == null ? "Default" : new java.io.File(musicFilePath).getName());
    }
}