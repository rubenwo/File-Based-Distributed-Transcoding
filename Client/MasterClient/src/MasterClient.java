import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class MasterClient {
    public static final int PORT = 9000;
    public static final String HOSTNAME = "localhost";

    private HashMap<String, String> slavesMap;

    private Socket socket;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private OperatingSystem operatingSystem;

    private String clientName;

    public MasterClient(String clientName) {
        operatingSystem = OperatingSystem.detectOperatingSystem(System.getProperty("os.name"));
        this.clientName = clientName;
        openSocket();
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
        toServer.writeUTF(clientName);
        toServer.flush();
        toServer.writeUTF(socket.getInetAddress().toString());
        toServer.flush();

        try {
            slavesMap = (HashMap<String, String>) fromServer.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MasterClient("New Master");
    }
}
