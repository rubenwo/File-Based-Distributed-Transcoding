import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ThreadedServer implements Runnable, ClientStatusListener {
    private HashMap<String, String> slavesMap = new HashMap<>();
    private HashMap<String, String> mastersMap = new HashMap<>();
    private ServerSocket serverSocket;

    private boolean running = true;

    public ThreadedServer() {
        openServerSocket();
    }

    private void openServerSocket() {
        try {
            serverSocket = new ServerSocket(Server.PORT, 0, InetAddress.getByName(Server.HOSTNAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running) {
            System.out.println(slavesMap.size());
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                System.out.println("Connection established with client.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(new ConnectionHandler(socket, slavesMap, this)).start();
        }
    }

    @Override
    public void onSlaveOnline(String slaveClient, String slaveClientIp) {
        slavesMap.put(slaveClient, slaveClientIp);
    }

    @Override
    public void onSlaveOffline(String slaveClient) {
        slavesMap.remove(slaveClient);
    }

    @Override
    public void onMasterOnline(String masterClient, String masterClientIp) {
        mastersMap.put(masterClient, masterClientIp);
    }

    @Override
    public void onMasterOffline(String masterClient) {
        mastersMap.remove(masterClient);
    }

    @Override
    public void onError(Error error) {
        System.err.print(error.getMessage());
    }

    public void shutdown() {
        running = false;
    }
}
