import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class FileWriterProxy {
    private FileWriter fileWriter;
    private AtomicLong position;
    private long size;
    private String fileName;

    public FileWriterProxy(String path, FileMetaData metaData) throws IOException {
        assert !Objects.isNull(metaData) && !path.isEmpty();

        this.fileWriter = new FileWriter(path + "/" + metaData.getFileName());
        this.position = new AtomicLong(01);
        this.size = metaData.getSize();
        this.fileName = metaData.getFileName();
    }

    public FileWriter getFileWriter() {
        return fileWriter;
    }

    public AtomicLong getPosition() {
        return position;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean done() {
        return position.get() == this.size;
    }
}
