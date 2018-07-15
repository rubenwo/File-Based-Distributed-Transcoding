import javax.swing.*;
import java.awt.*;

public class SlaveFrame {
    private JTextArea currentJob;
    private JProgressBar progressBar;

    public SlaveFrame(String ip) {
        JFrame frame = new JFrame("SlaveFrame");
        frame.setPreferredSize(new Dimension(300, 110));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());

        JTextArea ipAddress = new JTextArea("IP-Address: " + ip);
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

    public void updateCurrentJob(double progress) {
        progressBar.setValue((int) progress);
        progressBar.setString("" + progress + "%");
    }

    public void setCurrentJobFileName(String fileName) {
        currentJob.setText("Current Job: " + fileName + "\nProgress:");
    }

    public void resetFrame() {
        //JOptionPane.showMessageDialog(null, "Transcode Job Done!", "Job Done!", JOptionPane.INFORMATION_MESSAGE);

        currentJob.setText("Current Job: None\nProgress:");
        progressBar.setValue(0);
        progressBar.setString("N/A");
    }
}
