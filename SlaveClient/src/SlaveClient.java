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

    private String tempDir;

    private String ffmpegCommand;
    private String inputFileExtension;
    private String outputFileExtension;

    public SlaveClient(String ServerIP) {
        operatingSystem = OperatingSystem.detectOperatingSystem();
        this.ID = UUID.randomUUID().toString();
        this.clientId = "Slave ID: " + ID;

        openSocket(ServerIP);

        System.out.println("Creating temporary directory");
        try {
            tempDir = createTempDir();
            createEncoder();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    private String createTempDir() throws IOException {
        Path tempDir = Files.createTempDirectory(this.ID);
        return tempDir.toString() + "/";
    }

    public ObjectInputStream getFromServer() {
        return fromServer;
    }

    public void receiveFile() {
        try {
            int port = fromServer.readInt();
            long fileSize = fromServer.readLong();

            inputFileExtension = fromServer.readUTF();
            outputFileExtension = fromServer.readUTF();

            new Thread(new FileReceiver(fileSize, port, inputFileExtension, outputFileExtension, this, this.tempDir)).start();
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

    private void returnTranscodedFile() {
        System.out.println("Returning file...");
    }

    private void doCleanUp() throws IOException {
        System.out.println("Cleaning up temp directory...");
        Path untranscoded = Paths.get(tempDir + "Untranscoded" + inputFileExtension);
        Path transcoded = Paths.get(tempDir + "Transcoded" + outputFileExtension);

        Files.delete(untranscoded);
        Files.delete(transcoded);
    }

    @Override
    public void onJobDone() {
        System.out.println("Done transcoding!");
        System.out.println("Resetting frame...");
        slaveFrame.resetFrame();
        returnTranscodedFile();
        try {
            doCleanUp();

            toServer.writeByte(4);
            toServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void createEncoder() {
        String encoderPath = tempDir + "ffmpeg";
        InputStream ffmpegLoader = getClass().getResourceAsStream(OperatingSystem.getEncoderPath(operatingSystem));
        Path path = Paths.get(encoderPath);
        try {
            System.out.println("Copying ffmpeg to temp dir...");
            Files.copy(ffmpegLoader, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileReceived(String input, String output) {
        String encoderPath = tempDir + "ffmpeg";
        startEncoding(encoderPath, input, output);
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
