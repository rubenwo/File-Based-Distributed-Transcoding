package ruben.distributed_transcoding.Server.GUI;

import ruben.distributed_transcoding.Constants;

import javax.swing.*;
import java.awt.*;

public class ServerGUI {
    public ServerGUI(String ipAddress) {
        JFrame frame = new JFrame("ruben/distributed_transcoding/Server");
        frame.setPreferredSize(new Dimension(210, 75));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel contentPane = new JPanel(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setText("Server IP-Address: " + ipAddress + "\nServer Port: " + Constants.PORT);
        textArea.setEditable(false);

        contentPane.add(textArea, BorderLayout.CENTER);

        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
