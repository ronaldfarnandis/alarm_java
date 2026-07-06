import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Run UI creation on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Java Desktop Alarm Clock");
            AlarmManager manager = new AlarmManager();
            ClockPanel panel = new ClockPanel(manager);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(panel);
            frame.pack();
            frame.setSize(450, 400);
            frame.setLocationRelativeTo(null); // Center window
            frame.setVisible(true);
        });
    }
}