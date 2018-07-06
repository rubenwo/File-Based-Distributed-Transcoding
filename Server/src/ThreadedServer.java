import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ThreadedServer implements Runnable, SlaveListener {
    private ArrayList<String> onlineClients = new ArrayList<>();
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
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                System.out.println("Connection established with client.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(new ConnectionHandler(socket, onlineClients)).start();
        }
    }

    @Override
    public void onSlaveOnline(String slaveClient) {
        onlineClients.add(slaveClient);
    }

    @Override
    public void onSlaveOffline(String slaveClient) {
        onlineClients.removeIf(p -> p.equals(slaveClient));
    }

    @Override
    public void onError() {
        System.out.println("An error occurred.");
    }

    public void shutdown() {
        running = false;
    }
}
