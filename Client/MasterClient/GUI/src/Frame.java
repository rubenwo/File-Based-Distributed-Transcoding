import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Frame {

    public Frame(ArrayList<String> onlineClients) {
        JFrame frame = new JFrame("File-Based Distributed Transcoding Master");
        frame.setPreferredSize(new Dimension(1280, 720));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel contentPane = new JPanel(new BorderLayout());

        JPanel onlineClientPanel = new OnlineClientPanel(onlineClients);
        onlineClientPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

        JPanel ffmpegOptionPanel = new FFmpegOptionPanel();
        ffmpegOptionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

        contentPane.add(onlineClientPanel, BorderLayout.EAST);
        contentPane.add(ffmpegOptionPanel, BorderLayout.CENTER);

        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
