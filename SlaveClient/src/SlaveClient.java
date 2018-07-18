import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class SlaveClient implements ProgressListener, FFmpegJobRequestListener, FileReceiverListener {


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
        this.ID = UUID.randomUUID().toString();
        this.clientId = "Slave ID: " + ID;

        openSocket(ServerIP);

        System.out.println("Starting updater service");
        clientListenerService = new SlaveClientListenerService(this, this);
        clientListenerService.start();

        slaveFrame = new SlaveFrame(ServerIP, clientId);
    }

    private void openSocket(String HOSTNAME) {
        try {
            socket = new Socket(HOSTNAME, Constants.PORT);
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
        toServer.writeUTF(socket.getInetAddress().getHostAddress());
        toServer.flush();
    }

    public ObjectInputStream getFromServer() {
        return fromServer;
    }


    private String createTempDir() throws IOException {
        Path tempDir = Files.createTempDirectory(ID);
        return tempDir.toString() + "/";
    }

    public void receiveFile() {

        try {
            int port = fromServer.readInt();
            long fileSize = fromServer.readLong();

            String inputFileExtension = fromServer.readUTF();
            String outputFilesExtension = fromServer.readUTF();

            new Thread(new FileReceiver(UUID.randomUUID().toString(), fileSize, port, inputFileExtension, outputFilesExtension, this)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onSocketBound() {
        try {
            toServer.writeByte(3);
            toServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createEncoderPath(String tempDir) {
        String encoderPath = tempDir + "ffmpeg" + OperatingSystem.getEncoderExtension(operatingSystem);
        InputStream is = getClass().getResourceAsStream(OperatingSystem.getEncoderPath(operatingSystem));
        Path path = Paths.get(encoderPath);
        try {
            System.out.println("Copying ffmpeg");
            Files.copy(is, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encoderPath;
    }

    @Override
    public void onFileReceived(String input, String output, String tempDir) {
        startEncoding(createEncoderPath(tempDir), input, output);
    }

    private void startEncoding(String encoderPath, String input, String output) {
        System.out.println("Starting encoder...");
        FFmpegHandler ffmpegHandler = new FFmpegHandler(encoderPath, input, ffmpegCommand, output, this);
        new Thread(ffmpegHandler).start();
    }

    public static void main(String[] args) {
        try {
            new SlaveClient(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


}
