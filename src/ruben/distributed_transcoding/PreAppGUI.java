package ruben.distributed_transcoding;

import ruben.distributed_transcoding.MasterClient.GUI.PreMasterFrame;
import ruben.distributed_transcoding.Server.Server;
import ruben.distributed_transcoding.SlaveClient.GUI.PreSlaveFrame;

import javax.swing.*;
import java.awt.*;

public class PreAppGUI {
    public PreAppGUI() {
        JFrame frame = new JFrame("Start");
        frame.setPreferredSize(new Dimension(200, 250));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        JPanel contentPane = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        for (JButton button : getButtons())
            buttonPanel.add(button);

        contentPane.add(buttonPanel);

        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JButton[] getButtons() {
        JButton[] buttons = new JButton[3];
        buttons[0] = new JButton("Create a Server");
        buttons[1] = new JButton("Create a Master GUI");
        buttons[2] = new JButton("Create A Slave GUI");

        buttons[0].addActionListener(e -> new Server());
        buttons[1].addActionListener(e -> new PreMasterFrame());
        buttons[2].addActionListener(e -> new PreSlaveFrame());

        for (JButton button : buttons) {
            button.setPreferredSize(new Dimension(200, 75));
            button.setHorizontalTextPosition(SwingConstants.CENTER);
        }

        return buttons;
    }

    public static void main(String[] args) {
        new PreAppGUI();
    }
}
