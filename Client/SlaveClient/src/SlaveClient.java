import java.io.IOException;
import java.net.Socket;

public class SlaveClient {
    private Socket socket;

    private OperatingSystem operatingSystem;

    public SlaveClient() {
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
            socket = new Socket(MasterClient.HOSTNAME, MasterClient.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new SlaveClient();
    }
}
