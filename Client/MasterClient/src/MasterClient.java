import java.io.IOException;
import java.net.Socket;

public class MasterClient {
    public static final int PORT = 9000;
    public static final String HOSTNAME = "localhost";

    private Socket socket;
    private OperatingSystem operatingSystem;

    public MasterClient() {
        operatingSystem = OperatingSystem.detectOperatingSystem(System.getProperty("os.name"));
        try {
            Process proc = Runtime.getRuntime().exec(OperatingSystem.getEncoderPath(operatingSystem) + " -hwaccels");
        } catch (IOException e) {
            e.printStackTrace();
        }
        openSocket();
    }

    private void openSocket() {
        try {
            socket = new Socket(HOSTNAME, PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new MasterClient();
    }
}
