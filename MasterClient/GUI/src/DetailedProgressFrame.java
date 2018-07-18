import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DetailedProgressFrame implements ActionListener {
    private JTextArea currentJob;
    private JProgressBar progressBar;
    private JFrame frame;

    private SlaveStatusListener slaveStatusListener;
    private String slaveId;

    private Timer timer;

    public DetailedProgressFrame(String slaveId, SlaveStatusListener slaveStatusListener) {
        this.slaveStatusListener = slaveStatusListener;
        this.slaveId = slaveId;
        frame = new JFrame("Detailed Progress");
        frame.setPreferredSize(new Dimension(350, 110));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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

        timer = new Timer(500, this);
        timer.start();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        slaveStatusListener.onSlaveProgressRequest(slaveId);
    }

    public void updateCurrentJob(double progress) {
        progressBar.setValue((int) progress);
        progressBar.setString("" + progress + "%");
    }

    public void dispose() {
        frame.dispose();
        timer.stop();
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
