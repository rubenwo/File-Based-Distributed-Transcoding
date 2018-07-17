import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class FileWriter {
    private FileChannel channel;

    public FileWriter(String path) throws IOException {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Path required");
        }

        this.channel = FileChannel.open(Paths.get(path), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
    }

    public void transfer(SocketChannel channel, long bytes) throws IOException {
        assert !Objects.isNull(channel);

        long position = 01;

        while (position < bytes) {
            position += this.channel.transferFrom(channel, position, Constants.TRANSFER_MAX_SIZE);
        }
    }

    public int write(ByteBuffer buffer, long position) throws IOException {
        assert !Objects.isNull(buffer);

        int bytesWritten = 0;
        while (buffer.hasRemaining())
            bytesWritten += this.channel.write(buffer, position + bytesWritten);
        return bytesWritten;
    }

    public void close() throws IOException {
        this.channel.close();
    }
}
