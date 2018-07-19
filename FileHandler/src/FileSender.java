import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSender implements Runnable {
    private long fileSize;
    private String fileName;
    private int port;
    private String ipAddress;

    private SlaveClient slaveClient;

    private SocketChannel socketChannel;

    private int bufferSize = 32 * 1024;

    public FileSender(long fileSize, String fileName, int port, String ipAddress) throws IOException {
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.port = port;
        this.ipAddress = ipAddress;

        socketChannel = this.createChannel();
    }

    public FileSender(long fileSize, String fileName, int port, String ipAddress, SlaveClient slaveClient) throws IOException {
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.port = port;
        this.ipAddress = ipAddress;
        this.slaveClient = slaveClient;

        socketChannel = this.createChannel();
    }


    @Override
    public void run() {
        try {
            sendFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    private void sendFile() throws IOException {
        Path path = Paths.get(fileName);
        FileChannel fileChannel = FileChannel.open(path);

        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        while (fileChannel.read(buffer) > 0) {
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
        }
        //if(slaveClient != null)
        //   slaveClient.doCleanUp();
        socketChannel.close();
    }

    private SocketChannel createChannel() throws IOException {
        System.out.println(this.port);
        SocketChannel socketChannel = SocketChannel.open();
        SocketAddress socketAddress = new InetSocketAddress(this.ipAddress, this.port);
        socketChannel.connect(socketAddress);
        return socketChannel;
    }
}
