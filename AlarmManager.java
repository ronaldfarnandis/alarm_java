import java.time.LocalTime;
import java.util.ArrayList;

public class AlarmManager {
    private ArrayList<Alarm> alarms;
    private MusicPlayer player;
    private Alarm activeAlarm;

    public AlarmManager() {
        this.alarms = FileManager.loadAlarms();
        this.player = new MusicPlayer();
    }

    public void addAlarm(Alarm alarm) {
        alarms.add(alarm);
        FileManager.saveAlarms(alarms);
    }

    public void removeAlarm(int index) {
        if (index >= 0 && index < alarms.size()) {
            alarms.remove(index);
            FileManager.saveAlarms(alarms);
        }
    }

    public ArrayList<Alarm> getAlarms() {
        return alarms;
    }

    // Phase 2: Checked every second by ClockPanel/Main thread
    public void checkAlarms(LocalTime currentTime) {
        for (Alarm alarm : alarms) {
            if (alarm.isEnabled() && 
                alarm.getTime().getHour() == currentTime.getHour() && 
                alarm.getTime().getMinute() == currentTime.getMinute() && 
                currentTime.getSecond() == 0) {
                
                triggerAlarm(alarm);
            }
        }
    }

    private void triggerAlarm(Alarm alarm) {
        activeAlarm = alarm;
        player.play(alarm.getMusicFilePath());
    }

    public void stopAlarm() {
        player.stop();
        activeAlarm = null;
    }

    // Phase 8: Snooze implementation
    public void snoozeAlarm(int minutes) {
        if (activeAlarm != null) {
            player.stop();
            // Create a temporary/one-off delayed alarm
            LocalTime snoozeTime = LocalTime.now().plusMinutes(minutes);
            Alarm snoozeAlarm = new Alarm(snoozeTime, activeAlarm.getMusicFilePath());
            alarms.add(snoozeAlarm);
            activeAlarm = null;
        }
    }
}