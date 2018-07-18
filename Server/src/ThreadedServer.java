import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ThreadedServer implements Runnable, ClientStatusListener, SlaveProgressListener, FFmpegCommandListener {
    private JobDistributingManager distributingManager;
    private ArrayList<String> onlineSlaveIds = new ArrayList<>();
    private ArrayList<ConnectionHandler> slaveHandlers = new ArrayList<>();
    private ArrayList<ConnectionHandler> masterHandlers = new ArrayList<>();

    private HashMap<String, Double> slaveProgress = new HashMap<>();

    private ServerSocket serverSocket;

    private boolean running = true;

    public ThreadedServer(String IpAddress) {
        distributingManager = new JobDistributingManager(slaveHandlers);
        openServerSocket(IpAddress);
    }

    private void openServerSocket(String IpAddress) {
        try {
            serverSocket = new ServerSocket(Constants.PORT, 0, InetAddress.getByName(IpAddress));
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
            new Thread(new ConnectionHandler(socket, onlineSlaveIds, slaveProgress, this, this, this)).start();
        }
    }

    @Override
    public void onSlaveOnline(ConnectionHandler slaveHandler) {
        slaveHandlers.add(slaveHandler);
        onlineSlaveIds.add(slaveHandler.getClientId());
        slaveProgress.put(slaveHandler.getClientId(), 0.0);
        distributingManager.updateOnlineSlavesList(slaveHandlers);
        for (ConnectionHandler master : masterHandlers) {
            master.updateMasterClients(onlineSlaveIds);
            master.updateProgressMap(slaveProgress);
        }
    }

    @Override
    public void onSlaveOffline(ConnectionHandler slaveHandler) {
        slaveHandlers.removeIf(p -> p.equals(slaveHandler));
        onlineSlaveIds.removeIf(p -> p.equals(slaveHandler.getClientId()));
        distributingManager.updateOnlineSlavesList(slaveHandlers);
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

    @Override
    public void onCommandsReceived(String[] commands) {
        String[] inputs = new String[commands.length];
        for (int i = 0; i < commands.length; i++)
            inputs[i] = commands[i].split(",")[0];
        String ffmpegCommand = commands[0].split(",")[1];

        distributingManager.setCommand(ffmpegCommand);
        distributingManager.setInputs(inputs);
        distributingManager.distributeJobs();
    }

    public void shutdown() {
        running = false;
    }
}
