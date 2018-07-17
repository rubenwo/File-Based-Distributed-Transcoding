import javax.swing.*;

public class MasterLoginFrame {
    public MasterLoginFrame() {
        JFrame frame = new JFrame("Master Client Login");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        JButton createMaster = new JButton("Master");
        contentPane.add(createMaster);

        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
