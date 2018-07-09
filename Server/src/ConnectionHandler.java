import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;

    private ClientStatusListener clientStatusListener;

    private String clientType;
    private String clientId;

    public ConnectionHandler(Socket socket, ClientStatusListener clientStatusListener) {
        System.out.println("Connection Handler setting up...");

        this.socket = socket;
        this.clientStatusListener = clientStatusListener;

        openStreams();

        System.out.println("Connection Handler set-up.");
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
        clientType = fromClient.readUTF();
        switch (clientType) {
            case "MasterClient":
                System.out.println("A Master client just came online.");
                clientStatusListener.onMasterOnline(this);
                break;
            case "SlaveClient":
                System.out.println("A slave encoder just came online.");
                clientStatusListener.onSlaveOnline(this);
                break;
        }
        clientId = fromClient.readUTF();
    }

    @Override
    public void run() {
        System.out.println("Connection Handler is running...");
    }

    public void updateMasterClients(ArrayList<ConnectionHandler> slaveHandlers) {
        ArrayList<String> slaveNames = new ArrayList<>();
        for (ConnectionHandler slave : slaveHandlers)
            slaveNames.add(slave.getClientId());
        try {
            toClient.writeObject(slaveNames);
            toClient.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientId() {
        return clientId;
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
