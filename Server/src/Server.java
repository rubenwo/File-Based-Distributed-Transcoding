import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server {
    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        Constants.PORTS = new int[1000];
        for (int i = 0; i < Constants.PORTS.length; i++)
            Constants.PORTS[i] = 9001 + i;
        String ipAddress = null;
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        new ServerGUI(ipAddress);
        new Thread(new ThreadedServer(ipAddress)).start();

    }
}
