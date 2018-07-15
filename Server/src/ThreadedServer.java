import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ThreadedServer implements Runnable, ClientStatusListener, SlaveProgressListener {
    private ArrayList<String> onlineSlaveIds = new ArrayList<>();
    private ArrayList<ConnectionHandler> slaveHandlers = new ArrayList<>();
    private ArrayList<ConnectionHandler> masterHandlers = new ArrayList<>();

    private HashMap<String, Double> slaveProgress = new HashMap<>();

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
            new Thread(new ConnectionHandler(socket, onlineSlaveIds, slaveProgress, this, this)).start();
        }
    }

    @Override
    public void onSlaveOnline(ConnectionHandler slaveHandler) {
        slaveHandlers.add(slaveHandler);
        onlineSlaveIds.add(slaveHandler.getClientId());
        slaveProgress.put(slaveHandler.getClientId(), 0.0);
        for (ConnectionHandler master : masterHandlers) {
            master.updateMasterClients(onlineSlaveIds);
            master.updateProgressMap(slaveProgress);
        }
    }

    @Override
    public void onSlaveOffline(ConnectionHandler slaveHandler) {
        slaveHandlers.removeIf(p -> p.equals(slaveHandler));
        onlineSlaveIds.removeIf(p -> p.equals(slaveHandler.getClientId()));
        for (ConnectionHandler master : masterHandlers)
            master.updateMasterClients(onlineSlaveIds);
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

    @Override
    public void onSlaveProgressAvailable(String slaveId, double progress) {
        slaveProgress.put(slaveId, progress);
        for (ConnectionHandler master : masterHandlers)
            master.updateProgressMap(slaveProgress);
    }

    public void shutdown() {
        running = false;
    }
}
