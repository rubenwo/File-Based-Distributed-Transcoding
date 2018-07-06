import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionHandler implements Runnable {

    private ArrayList<String> onlineCLients;

    private Socket socket;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;

    private String clientType;

    public ConnectionHandler(Socket socket, ArrayList<String> onlineClients) {
        System.out.println("Connection Handler setting up.");
        this.onlineCLients = onlineClients;
        this.socket = socket;
        System.out.println(this.socket);
        openStreams();
        System.out.println("Connection Handler set-up.");
    }

    private void openStreams() {
        System.out.println("Opening streams...");
        try {
            fromClient = new ObjectInputStream(socket.getInputStream());
            System.out.println("Input set-up.");
            toClient = new ObjectOutputStream(socket.getOutputStream());
            toClient.flush();
            System.out.println("Output set-up.");

            clientType = fromClient.readUTF();

            initClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initClient() throws IOException {
        if (clientType.equals("MasterClient")) {
            toClient.writeObject(onlineCLients);
            toClient.flush();
        } else if (clientType.equals("SlaveClient")) {
            System.out.println("Slave connected");
        } else {
            System.err.println("This client is not supported!");
        }
    }

    @Override
    public void run() {

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
