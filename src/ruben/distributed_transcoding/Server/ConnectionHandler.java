package ruben.distributed_transcoding.Server;

import ruben.distributed_transcoding.Constants;
import ruben.distributed_transcoding.FileHandler.FileReceiver;
import ruben.distributed_transcoding.FileHandler.FileReceiverListener;
import ruben.distributed_transcoding.FileHandler.FileSender;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionHandler implements Runnable, FileReceiverListener {
    private ArrayList<String> onlineSlaveIds;
    private StatusEnum status = StatusEnum.IDLE;
    private HashMap<String, Double> slaveProgress;
    private Socket socket;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;

    private Path outputDir;

    private ClientStatusListener clientStatusListener;

    private String clientId;
    private String clientIP;

    public ConnectionHandler(Socket socket, ArrayList<String> onlineSlaveIds, HashMap<String, Double> slaveProgress, ClientStatusListener clientStatusListener, SlaveProgressListener slaveProgressListener, FFmpegCommandListener fFmpegCommandListener) {
        System.out.println("Connection Handler setting up...");

        this.socket = socket;
        this.slaveProgress = slaveProgress;
        this.onlineSlaveIds = onlineSlaveIds;
        this.clientStatusListener = clientStatusListener;

        openStreams();

        System.out.println("Connection Handler set-up.");
        new Thread(new ServerListenerService(this, slaveProgressListener, fFmpegCommandListener)).start();
    }

    private void openStreams() {
        System.out.println("Opening Object streams");
        try {
            fromClient = new ObjectInputStream(socket.getInputStream());
            toClient = new ObjectOutputStream(socket.getOutputStream());
            toClient.flush();
            initClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initClient() throws IOException {
        System.out.println("Initializing Client...");
        String clientType = fromClient.readUTF();
        switch (clientType) {
            case "MasterClient":
                System.out.println("A Master client just came online.");
                clientId = fromClient.readUTF();
                clientStatusListener.onMasterOnline(this);
                toClient.writeObject(onlineSlaveIds);
                toClient.flush();
                break;
            case "SlaveClient":
                System.out.println("A slave encoder just came online.");
                clientId = fromClient.readUTF();
                clientIP = fromClient.readUTF();
                System.out.println("Client IP: " + clientIP);
                clientStatusListener.onSlaveOnline(this);
                break;
        }
    }

    @Override
    public void run() {
        System.out.println("Connection Handler is running...");
    }

    public void updateMasterClients(ArrayList<String> onlineSlaves) {
        System.out.println("Updating Slave client List");
        String[] onlineSlavesArray = new String[onlineSlaves.size()];
        for (int i = 0; i < onlineSlavesArray.length; i++)
            onlineSlavesArray[i] = onlineSlaves.get(i);
        try {
            toClient.writeByte(0);
            toClient.flush();
            toClient.writeObject(onlineSlavesArray);
            toClient.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSocketBound() {
        try {
            toClient.writeByte(4);
            toClient.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileReceived(String input, String output) {

    }

    public String getClientId() {
        return clientId;
    }

    public ObjectInputStream getFromClient() {
        return fromClient;
    }

    public void updateProgressMap(HashMap<String, Double> slaveProgress) {
        this.slaveProgress = slaveProgress;
    }

    public void sendProgress(String clientRequestId) {
        try {
            toClient.writeByte(2);
            toClient.flush();
            double progress = slaveProgress.get(clientRequestId);
            toClient.writeDouble(progress);
            toClient.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCommandToSlave(String command) {
        try {
            toClient.writeByte(1);
            toClient.flush();
            toClient.writeUTF(command);
            toClient.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    private Object[] config = new Object[3];

    public void sendFile(String filename) {
        int port = Constants.PORTS[Constants.PORTS_INDEX];
        Constants.INCREASE_PORTS_INDEX();

        try {
            toClient.writeByte(3);
            toClient.flush();
            toClient.writeInt(port);
            toClient.flush();

            File file = new File(filename);

            toClient.writeLong(file.length());
            toClient.flush();
            toClient.writeUTF(file.getName());
            toClient.flush();
            toClient.writeUTF(".mkv");
            toClient.flush();

            config[0] = file.length();
            config[1] = file.getPath();
            config[2] = port;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startFileSenderThread() {
        System.out.println("Encoder IP: " + this.clientIP + " | Port: " + (int) config[2]);
        //SlaveClient IP required!
        try {
            new Thread(new FileSender((long) config[0], (String) config[1], (int) config[2], this.clientIP)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int port;

    public void returnCurrentPort() {
        try {
            port = Constants.PORTS[Constants.PORTS_INDEX];
            Constants.INCREASE_PORTS_INDEX();

            toClient.writeByte(5);
            toClient.flush();

            toClient.writeInt(port);
            toClient.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOutputDir(Path outputDir) {
        this.outputDir = outputDir;
        System.out.println(outputDir.toAbsolutePath());
    }


    public void startFileReceiver() throws IOException {
        //OWN IP
        long fileSize = fromClient.readLong();

        String inputFile = "/" + fromClient.readUTF();
        System.out.println(outputDir.toString() + inputFile);

        new Thread(new FileReceiver(fileSize, port, inputFile, null, this, outputDir.toString())).start();
    }
}
