import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class FileReader {
    private FileChannel channel;
    private FileSender sender;

    public FileReader(FileSender sender, String path) throws IOException {
        if (Objects.isNull(sender) || path.isEmpty())
            throw new IllegalArgumentException("Sender and Path are required");
        this.sender = sender;
        this.channel = FileChannel.open(Paths.get(path), StandardOpenOption.READ);
    }

    public void read() throws IOException {
        System.out.println("Read");
        try {
            transfer();
        } finally {
            close();
        }
    }

    public void close() throws IOException {
        this.sender.close();
        this.channel.close();
    }

    private void transfer() throws IOException {
        System.out.println("Transfer");
        this.sender.transfer(this.channel, 01, this.channel.size());
    }
}
