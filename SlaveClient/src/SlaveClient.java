import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class SlaveClient implements ProgressListener, FFmpegJobRequestListener {
    public static final int PORT = 9000;
    public static String HOSTNAME = "";

    private Socket socket;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;

    private SlaveClientListenerService clientListenerService;

    private OperatingSystem operatingSystem;

    private SlaveFrame slaveFrame;
    private String ID;
    private String clientId;

    private String ffmpegCommand;

    public SlaveClient(String ServerIP) {
        operatingSystem = OperatingSystem.detectOperatingSystem();
        HOSTNAME = ServerIP;
        this.ID = UUID.randomUUID().toString();
        this.clientId = "Slave ID: " + ID;

        openSocket();

        System.out.println("Starting updater service");
        clientListenerService = new SlaveClientListenerService(this, this);
        clientListenerService.start();

        slaveFrame = new SlaveFrame(ServerIP, clientId);
    }

    private void openSocket() {
        try {
            socket = new Socket(SlaveClient.HOSTNAME, SlaveClient.PORT);
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
        System.out.println(clientId);
        toServer.writeUTF("SlaveClient");
        toServer.flush();
        toServer.writeUTF(clientId);
        toServer.flush();
    }

    public ObjectInputStream getFromServer() {
        return fromServer;
    }

    private String getFileExtension(String filename) {
        try {
            return filename.substring(filename.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    private String createTempDir() throws IOException {
        Path tempDir = Files.createTempDirectory(ID);
        return tempDir.toString() + "/";
    }

    public void receiveFile() throws IOException {
        new Thread(new ReceiverRunnable()).start();
    }

    @Override
    public void onJobSubmitted(String fileName) {
        slaveFrame.setCurrentJobFileName(fileName);
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
        slaveFrame.updateCurrentJob(progress);
    }

    @Override
    public void onJobDone() {
        System.out.println("Done transcoding!");
        slaveFrame.resetFrame();
    }

    @Override
    public void onError(Error error) {
        System.out.println(error.toString());
    }

    @Override
    public void onJobRequest(String command) {
        this.ffmpegCommand = command;
    }

    private void startEncoding(String input, String output) {
        FFmpegHandler ffmpegHandler = new FFmpegHandler(operatingSystem, input, ffmpegCommand, output, this);
        new Thread(ffmpegHandler).start();
    }

    public static void main(String[] args) {
        new SlaveClient("192.168.2.125");
    }

    class ReceiverRunnable implements Runnable {
        @Override
        public void run() {
            try {
                String path = createTempDir();
                System.out.println(path);

                String filename = fromServer.readUTF();
                System.out.println("Filename: " + filename);
                Long fileSize = fromServer.readLong();
                System.out.println("File size: " + fileSize + "B");

                String fileExtension = getFileExtension(filename);

                String input = path + "Untranscoded." + fileExtension;
                String output = path + "Transcoded.mkv";

                byte[] buffer = new byte[1024];
                System.out.println("Receiving file...");
                FileOutputStream fileOutputStream = new FileOutputStream(new File(input), true);
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

                startEncoding(input, output);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
