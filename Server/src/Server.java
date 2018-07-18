import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server {
    public static final int PORT = 9000;
    public static String HOSTNAME = "";

    public static int[] PORTS;
    public static int PORTS_INDEX = 0;

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        try {
            HOSTNAME = InetAddress.getLocalHost().getHostAddress();
            PORTS = new int[1000];
            for (int i = 0; i < PORTS.length; i++)
                PORTS[i] = 9001 + i;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        new ServerGUI();
        new Thread(new ThreadedServer()).start();
    }
}
