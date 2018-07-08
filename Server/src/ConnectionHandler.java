import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class ConnectionHandler implements Runnable {
    private HashMap<String, String> slavesMap;

    private Socket socket;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;

    private ClientStatusListener clientStatusListener;

    public ConnectionHandler(Socket socket, HashMap<String, String> slavesMap, ClientStatusListener clientStatusListener) {
        System.out.println("Connection Handler setting up...");

        this.slavesMap = slavesMap;
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
        String clientType = fromClient.readUTF();
        switch (clientType) {
            case "MasterClient":
                String master = fromClient.readUTF();
                String masterIp = fromClient.readUTF();
                clientStatusListener.onMasterOnline(master, masterIp);
                toClient.writeObject(slavesMap);
                toClient.flush();
                break;
            case "SlaveClient":
                String slave = fromClient.readUTF();
                String slaveIp = fromClient.readUTF();
                clientStatusListener.onSlaveOnline(slave, slaveIp);
                break;
        }
    }

    @Override
    public void run() {
        System.out.println("Connection Handler is running...");
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
