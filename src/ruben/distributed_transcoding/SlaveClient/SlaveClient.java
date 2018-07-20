package ruben.distributed_transcoding.SlaveClient;

import ruben.distributed_transcoding.Constants;
import ruben.distributed_transcoding.FileHandler.FileReceiver;
import ruben.distributed_transcoding.FileHandler.FileReceiverListener;
import ruben.distributed_transcoding.FileHandler.FileSender;
import ruben.distributed_transcoding.SlaveClient.CLI.CommandLineInterface;
import ruben.distributed_transcoding.SlaveClient.GUI.SlaveFrame;

import java.io.*;
import java.net.Socket;
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
    private CommandLineInterface commandLineInterface;

    private String ID;
    private String clientId;

    private String tempDir;

    private String ffmpegCommand;
    private String inputFile;
    private String outputFile;

    private String serverIP;
    private String ownIP;

    public SlaveClient(String serverIP) {
        this.serverIP = serverIP;
        operatingSystem = OperatingSystem.detectOperatingSystem();
        this.ID = UUID.randomUUID().toString();
        this.clientId = "Slave ID: " + ID;

        openSocket(serverIP);

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

        slaveFrame = new SlaveFrame(serverIP, clientId);
    }

    public SlaveClient(String serverIP, CommandLineInterface commandLineInterface) {
        this.serverIP = serverIP;
        operatingSystem = OperatingSystem.detectOperatingSystem();
        this.ID = UUID.randomUUID().toString();
        this.clientId = "Slave ID: " + ID;

        openSocket(serverIP);

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

        this.commandLineInterface = commandLineInterface;
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
        this.ownIP = socket.getLocalAddress().getHostAddress();
        System.out.println("The IP read on the Slave: " + this.ownIP);
        toServer.writeUTF(this.ownIP);
        toServer.flush();
    }

    private String createTempDir() throws IOException {
        Path tempDir = Files.createTempDirectory(this.ID);
        return tempDir.toString() + "/";
    }

    private void createEncoder() throws IOException {
        String tempEncoder = tempDir + "ffmpeg";
        long start = System.currentTimeMillis();

        InputStream in = getClass().getResourceAsStream(OperatingSystem.getEncoderPath(operatingSystem));
        Path tempEncoderPath = Paths.get(tempEncoder);
        Files.copy(in, tempEncoderPath);
        in.close();

        long end = System.currentTimeMillis();
        System.out.println("Extracted in: " + (end - start) + " milliseconds");
    }

    public ObjectInputStream getFromServer() {
        return fromServer;
    }

    public void receiveFile() {
        //OWN IP
        try {
            int port = fromServer.readInt();
            long fileSize = fromServer.readLong();

            inputFile = fromServer.readUTF();
            String outputFileExtension = fromServer.readUTF();

            outputFile = "transcoded_" + inputFile.substring(0, inputFile.lastIndexOf(".")) + outputFileExtension;

            new Thread(new FileReceiver(fileSize, port, inputFile, outputFile, this, this.tempDir, this.ownIP)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long fileSize;
    private String fileName;
    private int port = -1;

    public void setPort(int port) {
        this.port = port;
        System.out.println("Port on slave is: " + port);
    }

    private void sendFile() throws IOException {
        toServer.writeByte(5);
        toServer.flush();

        File file = new File(tempDir + outputFile);
        System.out.println("sendFile(): " + file.getAbsolutePath());

        toServer.writeLong(file.length());
        toServer.flush();
        toServer.writeUTF(file.getName());

        fileSize = file.length();
        fileName = file.getAbsolutePath();
    }

    public void startFileSender() {
        System.out.println("Server IP: " + serverIP + " | Port: " + port);
        //Server IP address Required!
        try {
            new Thread(new FileSender(fileSize, fileName, port, this.serverIP, this)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onJobSubmitted(String fileName) {
        if (slaveFrame == null)
            System.out.println(fileName);
        else {
            slaveFrame.setCurrentJobFileName(fileName);
        }
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
        if (slaveFrame == null)
            System.out.println(progress + "%");
        else {
            slaveFrame.updateCurrentJob(progress);
        }
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
        if (slaveFrame != null)
            slaveFrame.resetFrame();
        try {
            toServer.writeByte(6);
            toServer.flush();
            sendFile();

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
}
