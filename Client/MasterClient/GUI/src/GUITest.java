import java.util.ArrayList;

public class GUITest {
    public static void main(String[] args) {
        ArrayList<String> onlineClients = new ArrayList<>();
        onlineClients.add("client11");
        onlineClients.add("client12");
        onlineClients.add("client13");
        onlineClients.add("client14");
        onlineClients.add("client15");

        new Frame(onlineClients);
    }
}
