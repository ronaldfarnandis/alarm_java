import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class ClockPanel extends JPanel {
    private JLabel timeLabel;
    private DefaultListModel<Alarm> listModel;
    private JList<Alarm> alarmList;
    private AlarmManager alarmManager;
    private String selectedFilePath = ""; // Placeholder for chosen sound

    public ClockPanel(AlarmManager manager) {
        this.alarmManager = manager;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Phase 1: Digital Clock Display ---
        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        add(timeLabel, BorderLayout.NORTH);

        // --- Phase 7: Alarm List Display ---
        listModel = new DefaultListModel<>();
        updateAlarmListUI();
        alarmList = new JList<>(listModel);
        add(new JScrollPane(alarmList), BorderLayout.CENTER);

        // --- Control Panels ---
        JPanel controlPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        
        // Input Subpanel
        JPanel inputPanel = new JPanel(new FlowLayout());
        SpinnerNumberModel hourModel = new SpinnerNumberModel(12, 0, 23, 1);
        SpinnerNumberModel minModel = new SpinnerNumberModel(0, 0, 59, 1);
        JSpinner hourSpinner = new JSpinner(hourModel);
        JSpinner minSpinner = new JSpinner(minModel);
        
        inputPanel.add(new JLabel("Hour:"));
        inputPanel.add(hourSpinner);
        inputPanel.add(new JLabel("Min:"));
        inputPanel.add(minSpinner);
        
        // Phase 5: File Chooser Button
        JButton browseBtn = new JButton("Choose Sound (WAV)");
        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                selectedFilePath = chooser.getSelectedFile().getAbsolutePath();
                browseBtn.setText(chooser.getSelectedFile().getName());
            }
        });
        inputPanel.add(browseBtn);
        controlPanel.add(inputPanel);

        // CRUD Action Buttons
        JPanel actionButtonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Set Alarm");
        addBtn.addActionListener(e -> {
            int hour = (int) hourSpinner.getValue();
            int minute = (int) minSpinner.getValue();
            Alarm newAlarm = new Alarm(LocalTime.of(hour, minute), selectedFilePath);
            alarmManager.addAlarm(newAlarm);
            updateAlarmListUI();
        });

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> {
            int selectedIdx = alarmList.getSelectedIndex();
            if (selectedIdx != -1) {
                alarmManager.removeAlarm(selectedIdx);
                updateAlarmListUI();
            }
        });
        actionButtonPanel.add(addBtn);
        actionButtonPanel.add(deleteBtn);
        controlPanel.add(actionButtonPanel);

        // Phase 4 & 8: Stop / Snooze Action Row
        JPanel executionPanel = new JPanel(new FlowLayout());
        JButton stopBtn = new JButton("STOP");
        stopBtn.setBackground(Color.RED);
        stopBtn.setForeground(Color.WHITE);
        stopBtn.addActionListener(e -> alarmManager.stopAlarm());

        JButton snoozeBtn = new JButton("Snooze (5 Min)");
        snoozeBtn.setBackground(Color.ORANGE);
        snoozeBtn.addActionListener(e -> {
            alarmManager.snoozeAlarm(5);
            updateAlarmListUI();
        });

        executionPanel.add(stopBtn);
        executionPanel.add(snoozeBtn);
        controlPanel.add(executionPanel);

        add(controlPanel, BorderLayout.SOUTH);

        // --- Event-Driven & Concurrency Core ---
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::updateTime, 0, 1, TimeUnit.SECONDS);
    }

    private void updateTime() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        // Safely update Swing GUI from a background background execution thread
        SwingUtilities.invokeLater(() -> {
            timeLabel.setText(now.format(formatter));
            alarmManager.checkAlarms(now);
        });
    }

    private void updateAlarmListUI() {
        listModel.clear();
        for (Alarm a : alarmManager.getAlarms()) {
            listModel.addElement(a);
        }
    }
}