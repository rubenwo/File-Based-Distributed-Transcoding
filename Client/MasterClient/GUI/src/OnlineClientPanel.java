import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class OnlineClientPanel extends JPanel {
    private ArrayList<String> onlineClients;
    private JList onlineClientJList;

    public OnlineClientPanel(ArrayList<String> onlineClients) {
        this.onlineClients = onlineClients;
        buildClientList();
    }

    private void buildClientList() {
        onlineClientJList = new JList(onlineClients.toArray());

        onlineClientJList.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                System.out.println(onlineClientJList.getSelectedValue());
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        onlineClientJList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(onlineClientJList.getSelectedValue());
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        this.add(onlineClientJList);
    }

    public void addClient(String client) {
        onlineClients.add(client);
        onlineClientJList.setListData(onlineClients.toArray());
    }

    public void removeClient(String client) {
        onlineClients.removeIf(p -> p.equals(client));
        onlineClientJList.setListData(onlineClients.toArray());
    }
}
