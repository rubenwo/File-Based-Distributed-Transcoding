import java.io.*;
import java.net.Socket;

public class SlaveClient {
    private Socket socket;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;

    private OperatingSystem operatingSystem;

    public SlaveClient() {
        operatingSystem = OperatingSystem.detectOperatingSystem(System.getProperty("os.name"));
        try {
            Process proc = Runtime.getRuntime().exec(OperatingSystem.getEncoderPath(operatingSystem) + " -hwaccels");
        } catch (IOException e) {
            e.printStackTrace();
        }
        openSocket();
    }

    private void openSocket() {
        try {
            socket = new Socket(MasterClient.HOSTNAME, MasterClient.PORT);
            openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openStream() throws IOException {
        toServer = new ObjectOutputStream(socket.getOutputStream());
        toServer.flush();

        fromServer = new ObjectInputStream(socket.getInputStream());

    }

    private void receiveFile() throws IOException {
        String filename = fromServer.readUTF();
        System.out.println("Filename: " + filename);
        Long fileSize = fromServer.readLong();
        System.out.println("File size: " + fileSize + "B");

        String savedFile = ".\\Resources\\receivedTest.mp4";

        byte[] buffer = new byte[1024];
        System.out.println("Receiving file...");
        FileOutputStream fileOutputStream = new FileOutputStream(new File(savedFile), true);
        long bytesRead;
        long transferStart = System.currentTimeMillis();
        do {
            bytesRead = fromServer.read(buffer, 0, buffer.length);
            fileOutputStream.write(buffer, 0, buffer.length);
        } while (!(bytesRead < 1024));
        long transferEnd = System.currentTimeMillis();
        double elapsedTimeInSeconds = (transferEnd - transferStart) * 1000;
        double transferSpeed = fileSize / elapsedTimeInSeconds;
        System.out.println(transferSpeed + " MB/s");
        System.out.println("Finished receiving " + filename);
        fileOutputStream.close();
    }

    public static void main(String[] args) {
        new SlaveClient();
    }
}
