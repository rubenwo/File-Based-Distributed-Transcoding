import java.util.ArrayList;

public class GUITest {
    public static void main(String[] args) {
        ArrayList<String> onlineClients = new ArrayList<>();
        onlineClients.add("Client 1");
        onlineClients.add("Client 2");
        onlineClients.add("Client 3");
        onlineClients.add("Client 4");
        onlineClients.add("Client 5");

        new Frame(onlineClients);
    }
}
