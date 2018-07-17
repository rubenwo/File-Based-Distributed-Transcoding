import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public class FileSender {
    private InetSocketAddress hostAddress;
    private SocketChannel client;

    public FileSender(int port) throws IOException {
        this.hostAddress = new InetSocketAddress("localhost", port);
        this.client = SocketChannel.open(this.hostAddress);
    }

    public void transfer(FileChannel channel, long position, long size) throws IOException {
        assert !Objects.isNull(channel);
        System.out.println("in Transfer");
        while (position < size) {
            System.out.println("sending...");
            position += channel.transferTo(position, Constants.TRANSFER_MAX_SIZE, this.client);
        }
    }

    public SocketChannel getChannel() {
        return this.client;
    }

    public void close() throws IOException {
        this.client.close();
    }
}
