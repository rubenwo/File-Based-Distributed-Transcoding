import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ThreadedServer implements Runnable, ClientStatusListener {
    private ArrayList<String> slavesNames = new ArrayList<>();
    private ArrayList<ConnectionHandler> slaveHandlers = new ArrayList<>();
    private ArrayList<ConnectionHandler> masterHandlers = new ArrayList<>();

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
            System.out.println(slaveHandlers.size());
            System.out.println(masterHandlers.size());
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                System.out.println("Connection established with client.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(new ConnectionHandler(socket, this)).start();
        }
    }

    @Override
    public void onSlaveOnline(ConnectionHandler slaveHandler) {
        slaveHandlers.add(slaveHandler);
        for (ConnectionHandler master : masterHandlers)
            master.updateMasterClients(slaveHandlers);
    }

    @Override
    public void onSlaveOffline(ConnectionHandler slaveHandler) {
        slaveHandlers.removeIf(p -> p.equals(slaveHandler));
        for (ConnectionHandler master : masterHandlers)
            master.updateMasterClients(slaveHandlers);
    }

    @Override
    public void onMasterOnline(ConnectionHandler masterHandler) {
        masterHandlers.add(masterHandler);
    }

    @Override
    public void onMasterOffline(ConnectionHandler masterHandler) {
        masterHandlers.removeIf(p -> p.equals(masterHandler));
    }

    @Override
    public void onError(Error error) {
        error.printStackTrace();
    }

    public void shutdown() {
        running = false;
    }
}
