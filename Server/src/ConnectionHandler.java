import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionHandler implements Runnable {
    private ArrayList<String> onlineSlaveIds;
    private StatusEnum status = StatusEnum.IDLE;
    private HashMap<String, Double> slaveProgress;
    private Socket socket;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;

    private ClientStatusListener clientStatusListener;

    private String clientId;

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
        System.out.println(slaveProgress.size());
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

    public void sendFile(String filename) throws IOException {
        toClient.writeByte(3);
        toClient.flush();
        System.out.println("Sending File: " + filename);
        toClient.writeUTF(filename);
        toClient.flush();

        File file = new File(filename);
        FileInputStream fileInputStream = new FileInputStream(file);

        long fileSize = file.length();

        byte[] buffer = new byte[1024];

        int read;

        toClient.writeLong(fileSize);
        toClient.flush();

        System.out.println("File size: " + fileSize + "B");
        System.out.println("Buffer size: " + socket.getReceiveBufferSize());

        while ((read = fileInputStream.read(buffer)) != -1) {
            toClient.write(buffer, 0, read);
            toClient.flush();
        }

        fileInputStream.close();
        System.out.println("Finished sending: " + filename);
        toClient.flush();
    }
}
