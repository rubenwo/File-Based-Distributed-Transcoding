import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionHandler implements Runnable {
    private ArrayList<String> onlineSlaveIds;
    private Socket socket;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;

    private ClientStatusListener clientStatusListener;

    private String clientId;

    public ConnectionHandler(Socket socket, ArrayList<String> onlineSlaveIds, ClientStatusListener clientStatusListener) {
        System.out.println("Connection Handler setting up...");

        this.socket = socket;
        this.onlineSlaveIds = onlineSlaveIds;
        this.clientStatusListener = clientStatusListener;

        openStreams();

        System.out.println("Connection Handler set-up.");
        new Thread(new ServerListenerService(this)).start();
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

    public void updateMasterClients(ArrayList<String> onlineSlaveIds) {
        System.out.println("Updating Slave client List");
        System.out.println(onlineSlaveIds.size());
        try {
            toClient.writeByte(2);
            toClient.flush();
            toClient.writeObject(onlineSlaveIds);
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

    private void sendFile(String filename) throws IOException {
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
