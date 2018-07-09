import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MasterClient {
    public static final int PORT = 9000;
    public static final String HOSTNAME = "localhost";

    private ArrayList<String> onlineClients;

    private Socket socket;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;

    private ListenerService listenerService;

    private OperatingSystem operatingSystem;

    private String clientId;

    public MasterClient(String clientId) {
        operatingSystem = OperatingSystem.detectOperatingSystem(System.getProperty("os.name"));
        this.clientId = clientId;

        openSocket();

        System.out.println("Starting updater service...");
        listenerService = new ListenerService(fromServer);
        new Thread(listenerService).start();

        new Frame(onlineClients);
    }

    private void openSocket() {
        try {
            socket = new Socket(HOSTNAME, PORT);
            openStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openStreams() throws IOException {
        System.out.println("Opening Object streams...");
        toServer = new ObjectOutputStream(socket.getOutputStream());
        toServer.flush();

        fromServer = new ObjectInputStream(socket.getInputStream());

        initConnection();
        System.out.println("Client is set-up.");
    }

    private void initConnection() throws IOException {
        System.out.println("Initializing Connection with Server...");
        toServer.writeUTF("MasterClient");
        toServer.flush();
        toServer.writeUTF(clientId);
        toServer.flush();
    }

    public static void main(String[] args) {
        new MasterClient("New Master");
    }
}
