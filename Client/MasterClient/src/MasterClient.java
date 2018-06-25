import java.io.IOException;
import java.net.Socket;

public class MasterClient {
    public static final int PORT = 9000;
    public static final String HOSTNAME = "localhost";

    private Socket socket;
    private OperatingSystem operatingSystem = null;

    public MasterClient() {
        detectOperatingSystem();
        try {
            //Process proc = Runtime.getRuntime().exec(OperatingSystem.getEncoderPath(operatingSystem) + " -hwaccels");
            Runtime.getRuntime().exec("sh -c ls");
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

    private void detectOperatingSystem() {
        String osName = System.getProperty("os.name");
        System.out.println(osName);

        if (osName.contains("Windows")) {
            operatingSystem = OperatingSystem.WINDOWS;
        } else if (osName.contains("Mac")) {
            operatingSystem = OperatingSystem.MAC;
        } else if (osName.contains("Linux")) {
            operatingSystem = OperatingSystem.LINUX;
        } else {
            System.out.println("This Operating system is not supported");
        }
    }

    public static void main(String[] args) {
        new MasterClient();
    }
}
