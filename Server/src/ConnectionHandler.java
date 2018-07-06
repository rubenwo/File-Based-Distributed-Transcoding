import java.io.*;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;

    public ConnectionHandler(Socket socket) {
        System.out.println("Connection Handler setting up.");
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

            System.out.println("Sending test-file");
            sendFile(".\\Resources\\test2.mp4");
        } catch (IOException e) {
            e.printStackTrace();
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
