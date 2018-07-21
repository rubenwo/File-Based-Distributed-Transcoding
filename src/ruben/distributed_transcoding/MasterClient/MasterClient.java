package ruben.distributed_transcoding.MasterClient;

import ruben.distributed_transcoding.Constants;
import ruben.distributed_transcoding.MasterClient.GUI.MasterFrame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;

public class MasterClient implements CommandListener, SlaveStatusListener {
    private ArrayList<String> onlineClients;

    private Socket socket;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;

    private MasterFrame masterFrame;

    private MasterClientListenerService clientListenerService;

    private String clientId;

    public MasterClient(String ServerIP) {
        this.clientId = UUID.randomUUID().toString();

        openSocket(ServerIP);

        System.out.println("Starting updater service...");
        clientListenerService = new MasterClientListenerService(fromServer, this);
        new Thread(clientListenerService).start();

        masterFrame = new MasterFrame(onlineClients, this, this);
    }

    private void openSocket(String HOSTNAME) {
        try {
            socket = new Socket(HOSTNAME, Constants.PORT);
            openStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openStreams() throws IOException {
        System.out.println("Opening Object streams...");
        toServer = new ObjectOutputStream(socket.getOutputStream());
        toServer.flush();

        fromServer = new ObjectInputStream(socket.getInputStream());

        initConnection();
        System.out.println("Client is set-up.");
    }

    private void initConnection() throws IOException {
        System.out.println("Initializing Connection with Server...");
        toServer.writeUTF("MasterClient");
        toServer.flush();
        toServer.writeUTF(clientId);
        toServer.flush();

        try {
            onlineClients = (ArrayList<String>) fromServer.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCommandsAvailable(String[] commands) {
        try {
            toServer.writeByte(0);
            toServer.flush();
            toServer.writeObject(commands);
            toServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNoInputSelected() {
        System.out.println("No input selected!");
    }

    @Override
    public void onSlaveStatusChanged(ArrayList<String> onlineSlaves) {
        System.out.println(onlineSlaves.size());
        this.onlineClients = onlineSlaves;
        masterFrame.updateClientList(this.onlineClients);
    }

    @Override
    public void onSlaveProgressRequest(String slaveId) {
        try {
            toServer.writeByte(2);
            toServer.flush();
            toServer.writeUTF(slaveId);
            toServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSlaveProgressRequestAvailable(double progress) {
        masterFrame.updateProgressBar(progress);
    }

    public static void main(String[] args) {
        try {
            new MasterClient(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
