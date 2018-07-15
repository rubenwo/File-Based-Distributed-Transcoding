import javax.swing.*;
import java.awt.*;

public class DetailedProgressFrame {
    private JTextArea currentJob;
    private JProgressBar progressBar;

    public DetailedProgressFrame(String slaveId) {
        JFrame frame = new JFrame("Detailed Progress");
        frame.setPreferredSize(new Dimension(350, 110));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());

        JTextArea ipAddress = new JTextArea("Slave ID: " + slaveId);
        ipAddress.setEditable(false);

        currentJob = new JTextArea();
        currentJob.setEditable(false);
        currentJob.setText("Current Job: None\nProgress:");

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("N/A");

        contentPane.add(ipAddress, BorderLayout.NORTH);
        contentPane.add(currentJob, BorderLayout.CENTER);
        contentPane.add(progressBar, BorderLayout.SOUTH);

        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
