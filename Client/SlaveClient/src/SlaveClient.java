import java.io.*;
import java.net.Socket;

public class SlaveClient implements ProgressListener, FFmpegJobListener {
    private Socket socket;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;

    private SlaveClientListenerService clientListenerService;

    private OperatingSystem operatingSystem;

    private SlaveFrame slaveFrame;
    private String clientId;

    public SlaveClient(String clientId) {
        operatingSystem = OperatingSystem.detectOperatingSystem();
        this.clientId = clientId;

        openSocket();

        System.out.println("Starting updater service");
        clientListenerService = new SlaveClientListenerService(fromServer, this);
        new Thread(clientListenerService).start();

        slaveFrame = new SlaveFrame();
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
        System.out.println("Opening Object streams...");
        toServer = new ObjectOutputStream(socket.getOutputStream());
        toServer.flush();

        fromServer = new ObjectInputStream(socket.getInputStream());

        initConnection();
        System.out.println("Connection is set-up.");
    }

    private void initConnection() throws IOException {
        System.out.println("Initializing connection with Server...");
        toServer.writeUTF("SlaveClient");
        toServer.flush();
        toServer.writeUTF(clientId);
        toServer.flush();
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

    @Override
    public void onProgressUpdate(double progress) {
        try {
            toServer.writeByte(1);
            toServer.flush();
            toServer.writeDouble(progress);
            toServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        slaveFrame.updateCurrentJob("Test File.mkv", progress);
    }

    @Override
    public void onError(Error error) {
        System.out.println(error.toString());
    }

    @Override
    public void onJobRequest(String command) {
        FFmpegHandler fFmpegHandler = new FFmpegHandler(operatingSystem, command, this);
        new Thread(fFmpegHandler).start();
    }

    public static void main(String[] args) {
        new SlaveClient("New Slave");
    }
}
