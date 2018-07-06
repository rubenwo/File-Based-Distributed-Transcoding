import java.io.*;
import java.net.Socket;

public class MasterClient {
    public static final int PORT = 9000;
    public static final String HOSTNAME = "localhost";

    private Socket socket;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private OperatingSystem operatingSystem;

    public MasterClient() {
        operatingSystem = OperatingSystem.detectOperatingSystem(System.getProperty("os.name"));
        /*ry {
            Process proc = Runtime.getRuntime().exec(OperatingSystem.getEncoderPath(operatingSystem) + " -hwaccels");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
        toServer = new ObjectOutputStream(socket.getOutputStream());
        toServer.flush();

        fromServer = new ObjectInputStream(socket.getInputStream());
    }

    public static void main(String[] args) {
        new MasterClient();
    }
}
