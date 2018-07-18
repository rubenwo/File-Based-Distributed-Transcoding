import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSender implements Runnable{
    private long fileSize;
    private String fileName;
    private int port;
    private String clientIp;

    private SocketChannel socketChannel;

    public FileSender(long fileSize, String fileName, int port, String clientIp) throws IOException {
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.port = port;
        this.clientIp = clientIp;

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

    private void sendFile() throws IOException {
        Path path = Paths.get(fileName);
        FileChannel fileChannel = FileChannel.open(path);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (fileChannel.read(buffer) > 0) {
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
        }
        socketChannel.close();
    }

    private SocketChannel createChannel() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        SocketAddress socketAddress = new InetSocketAddress(this.clientIp, this.port);
        socketChannel.connect(socketAddress);
        return socketChannel;
    }
}
