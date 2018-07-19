package ruben.distributed_transcoding.SlaveClient;

import ruben.distributed_transcoding.Constants;
import ruben.distributed_transcoding.FileHandler.FileReceiver;
import ruben.distributed_transcoding.FileHandler.FileReceiverListener;
import ruben.distributed_transcoding.FileHandler.FileSender;
import ruben.distributed_transcoding.SlaveClient.GUI.SlaveFrame;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URISyntaxException;
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
    private String inputFile;
    private String outputFile;

    private String serverIP;

    private boolean returning;

    public static void infoBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    public SlaveClient(String serverIP) {
        this.serverIP = serverIP;
        operatingSystem = OperatingSystem.detectOperatingSystem();
        this.ID = UUID.randomUUID().toString();
        this.clientId = "Slave ID: " + ID;

        openSocket(serverIP);

        System.out.println("Creating temporary directory");
        try {
            tempDir = createTempDir();
            try {
                createEncoder();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Starting updater service");
        clientListenerService = new SlaveClientListenerService(this, this);
        clientListenerService.start();

        slaveFrame = new SlaveFrame(serverIP, clientId);
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
        System.out.println("Initializing connection with ruben.distributed_transcoding.Server.ruben.distributed_transcoding.Server...");
        System.out.println(clientId);
        toServer.writeUTF("ruben/distributed_transcoding/SlaveClient");
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

            inputFile = fromServer.readUTF();
            String outputFileExtension = fromServer.readUTF();

            outputFile = "transcoded_" + inputFile.substring(0, inputFile.lastIndexOf(".")) + outputFileExtension;

            new Thread(new FileReceiver(fileSize, port, inputFile, outputFile, this, this.tempDir)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object[] config = new Object[3];

    private void sendFile() {
        int port = Constants.PORTS[Constants.PORTS_INDEX];
        Constants.PORTS_INDEX++;
        try {
            toServer.writeByte(5);
            toServer.flush();

            toServer.writeInt(port);
            toServer.flush();

            File file = new File(tempDir + outputFile);
            System.out.println("sendFile(): " + file.getAbsolutePath());

            toServer.writeLong(file.length());
            toServer.flush();
            toServer.writeUTF(file.getName());

            config[0] = file.length();
            config[1] = file.getAbsolutePath();
            config[2] = port;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startFileSender() {
        System.out.println("Sending back file...");
        try {
            new Thread(new FileSender((long) config[0], (String) config[1], (int) config[2], this.serverIP, this)).start();
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

    public void doCleanUp() throws IOException {
        System.out.println("Cleaning up temp directory...");
        Path untranscoded = Paths.get(tempDir + inputFile);
        Path transcoded = Paths.get(tempDir + outputFile);

        Files.delete(untranscoded);
        Files.delete(transcoded);
    }

    @Override
    public void onJobDone() {
        System.out.println("Done transcoding!");
        System.out.println("Resetting frame...");
        slaveFrame.resetFrame();
        sendFile();
        try {
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

    private void createEncoder() throws IOException, URISyntaxException {
        String encoderPath = tempDir + "ffmpeg";
        System.out.println("Extracting ffmpeg...");

        long start = System.currentTimeMillis();

        Path path = Paths.get(encoderPath);
        System.out.println(getClass().getResource("/Windows/ffmpeg.exe"));
        Path ffmpegPath = Paths.get(getClass().getResource(OperatingSystem.getEncoderPath(operatingSystem)).toURI());
        Files.copy(ffmpegPath, path);

        long end = System.currentTimeMillis();
        System.out.println("Extracted in: " + (end - start) + " milliseconds");
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
