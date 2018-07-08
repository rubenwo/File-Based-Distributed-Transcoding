import java.util.HashMap;

public class GUITest {
    public static void main(String[] args) {
        HashMap<String, String> onlineClients = new HashMap<>();
        onlineClients.put("client11", "127.0.0.1");
        onlineClients.put("client12", "127.0.0.1");
        onlineClients.put("client13", "127.0.0.1");
        onlineClients.put("client14", "127.0.0.1");
        onlineClients.put("client15", "127.0.0.1");

        new Frame(onlineClients);
        //new MasterLoginFrame();
    }
}
