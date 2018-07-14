import javax.swing.*;
import java.awt.*;

public class SlaveFrame {
    private JTextArea currentJob;

    public SlaveFrame() {
        JFrame frame = new JFrame("SlaveFrame");
        frame.setPreferredSize(new Dimension(200, 75));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());
        currentJob = new JTextArea();
        currentJob.setEditable(false);
        currentJob.setText("Current Job: None\nProgress: N/A");

        contentPane.add(currentJob);

        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void updateCurrentJob(String fileName, double progress) {
        currentJob.setText("Current Job: " + fileName + "\nProgress: " + progress);
    }
}
