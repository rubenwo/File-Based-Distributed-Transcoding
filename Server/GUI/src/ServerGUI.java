import javax.swing.*;
import java.awt.*;

public class ServerGUI {
    public ServerGUI() {
        JFrame frame = new JFrame("SlaveFrame");
        frame.setPreferredSize(new Dimension(210, 55));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel contentPane = new JPanel(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setText("Server IP-Address: " + Server.HOSTNAME + "\nServer Port: " + Server.PORT);
        textArea.setEditable(false);

        contentPane.add(textArea, BorderLayout.CENTER);

        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
