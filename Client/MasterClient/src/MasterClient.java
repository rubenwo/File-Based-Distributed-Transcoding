import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MasterClient implements CommandListener {
    public static final int PORT = 9000;
    public static final String HOSTNAME = "localhost";

    private ArrayList<String> onlineClients;

    private Socket socket;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;

    private ClientListenerService clientListenerService;

    private OperatingSystem operatingSystem;

    private String clientId;

    public MasterClient(String clientId) {
        operatingSystem = OperatingSystem.detectOperatingSystem(System.getProperty("os.name"));
        this.clientId = clientId;

        openSocket();

        System.out.println("Starting updater service...");
        clientListenerService = new ClientListenerService(fromServer);
        new Thread(clientListenerService).start();

        new Frame(onlineClients, this);
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

        try {
            onlineClients = (ArrayList<String>) fromServer.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCommandsAvailable(String[] commands) {
        String[] ffmpegCommands = new String[commands.length];
        for (int i = 0; i < commands.length; i++)
            ffmpegCommands[i] = OperatingSystem.getEncoderPath(operatingSystem) + commands[i];
        try {
            toServer.writeByte(0);
            toServer.flush();
            toServer.writeObject(ffmpegCommands);
            toServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNoInputSelected() {
        System.out.println("No input selected!");
    }

    public static void main(String[] args) {
        new MasterClient("New Master");
    }
}
