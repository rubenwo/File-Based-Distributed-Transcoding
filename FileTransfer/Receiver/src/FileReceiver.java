import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public class FileReceiver {
    private int port;
    private FileWriter fileWriter;
    private long size;

    public FileReceiver(int port, FileWriter fileWriter, long size) {
        this.port = port;
        this.fileWriter = fileWriter;
        this.size = size;
    }

    public void receive() throws IOException {
        SocketChannel channel = null;
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            init(serverSocketChannel);
            channel = serverSocketChannel.accept();
            doTransfer(channel);
        } finally {
            if (!Objects.isNull(channel))
                channel.close();
        }
        this.fileWriter.close();
    }

    private void doTransfer(SocketChannel channel) throws IOException {
        assert !Objects.isNull(channel);

        this.fileWriter.transfer(channel, this.size);
    }

    private void init(ServerSocketChannel serverSocketChannel) throws IOException {
        assert !Objects.isNull(serverSocketChannel);

        serverSocketChannel.bind(new InetSocketAddress("localhost", port));
    }
}

