import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server {
    public static final int PORT = 9000;
    public static String HOSTNAME = "";

    public static void main(String[] args) {
        try {
            HOSTNAME = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        new ServerGUI();
        new Thread(new ThreadedServer()).start();
    }
}
