package ruben.distributed_transcoding.FileHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class FileReceiver implements Runnable {
    private long fileSize;
    private int port;

    private FileReceiverListener fileReceiverListener;
    private ServerSocketChannel serverSocket;
    private SocketChannel socketChannel;

    private int bufferSize = 32 * 1024;

    private String tempDir;
    private String inputFile;
    private String outputFile;

    private String ipAddress = null;


    public FileReceiver(long fileSize, int port, String inputFile, String outputFile, FileReceiverListener fileReceiverListener, String tempDir) {
        this.fileReceiverListener = fileReceiverListener;
        this.fileSize = fileSize;
        this.port = port;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.tempDir = tempDir;
    }

    public FileReceiver(long fileSize, int port, String inputFile, String outputFile, FileReceiverListener fileReceiverListener, String tempDir, String ipAddress) {
        this.fileReceiverListener = fileReceiverListener;
        this.fileSize = fileSize;
        this.port = port;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.tempDir = tempDir;
        this.ipAddress = ipAddress;
    }

    @Override
    public void run() {
        try {
            this.socketChannel = createServerSocketChannel();
            receiveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    private void receiveFile() throws IOException {
        System.out.println(tempDir);
        String input = tempDir + inputFile;
        String output = tempDir + outputFile;

        Path path = Paths.get(input);
        FileChannel fileChannel = FileChannel.open(path, EnumSet.of(StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE));

        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        double transferSpeed = 0;
        long start = System.currentTimeMillis();

        while ((socketChannel.read(buffer)) > 0) {
            buffer.flip();
            fileChannel.write(buffer);
            buffer.clear();
        }

        long end = System.currentTimeMillis();
        long difference = end - start;
        transferSpeed = (double) this.fileSize / (difference / 1000);

        System.out.println((transferSpeed / 1000 / 1000) + "MB/s");
        fileChannel.close();
        System.out.println("File Received!");
        fileReceiverListener.onFileReceived(input, output);
        socketChannel.close();
        serverSocket.close();
    }

    private SocketChannel createServerSocketChannel() throws IOException {
        System.out.println("Creating serversocket...");
        SocketChannel client = null;
        serverSocket = ServerSocketChannel.open();
        System.out.println("serverSocket opened");
        System.out.println(this.port);
        if (ipAddress == null)
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        serverSocket.socket().bind(new InetSocketAddress(this.ipAddress, this.port));
        System.out.println("Bound serverSocket to: " + ipAddress);
        fileReceiverListener.onSocketBound();
        client = serverSocket.accept();

        System.out.println("Connection established!");
        return client;
    }
}
