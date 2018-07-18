import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class FileReceiver implements Runnable {
    private long fileSize;
    private int port;
    private String ID;

    private ServerSocketChannel serverSocket;
    private SocketChannel socketChannel;

    private String inputFileExtension;
    private String outputFileExtension;

    public FileReceiver(String ID, long fileSize, int port, String inputFileExtension, String outputFileExtension) throws IOException {
        this.ID = ID;
        this.fileSize = fileSize;
        this.port = port;
        this.inputFileExtension = inputFileExtension;
        this.outputFileExtension = outputFileExtension;

        this.socketChannel = createServerSocketChannel();
    }

    private String createTempDir() throws IOException {
        Path tempDir = Files.createTempDirectory(this.ID);
        return tempDir.toString() + "/";
    }

    @Override
    public void run() {
        try {
            receiveFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveFile() throws IOException {
        String tempDirPath = createTempDir();
        System.out.println(tempDirPath);
        String input = tempDirPath + "Untranscoded" + inputFileExtension;
        String output = tempDirPath + "Transcoded" + outputFileExtension;

        Path path = Paths.get(input);
        FileChannel fileChannel = FileChannel.open(path, EnumSet.of(StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE));

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        double transferSpeed = 0;
        long size = this.fileSize;
        long start = System.currentTimeMillis();

        while ((socketChannel.read(buffer)) > 0) {
            buffer.flip();
            fileChannel.write(buffer);
            buffer.clear();
        }

        long end = System.currentTimeMillis();
        long difference = end - start;
        transferSpeed = (double) size / (difference / 1000);

        System.out.println((transferSpeed / 1000 / 1000) + "MB/s");
        fileChannel.close();
        System.out.println("File Received!");
        socketChannel.close();
        serverSocket.close();
    }

    private SocketChannel createServerSocketChannel() throws IOException {
        SocketChannel client = null;
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), this.port));
        client = serverSocket.accept();

        System.out.println("Connection established!");
        return client;
    }
}
