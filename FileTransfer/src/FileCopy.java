import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class FileCopy {
    private FileCopy() {
        throw new IllegalStateException(Constants.INSTANTIATION_NOT_ALLOWED);
    }

    public static void copy(String src, String dest) throws IOException {
        if (src.isEmpty() || dest.isEmpty())
            throw new IllegalArgumentException("src and dest are required!");

        String fileName = getFileName(src);

        try (FileChannel from = (FileChannel.open(Paths.get(src), StandardOpenOption.READ));
             FileChannel to = (FileChannel.open(Paths.get(dest + "/" + fileName), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE))) {
            transfer(from, to, 0l, from.size());
        }
    }

    private static String getFileName(String src) {
        assert !src.isEmpty();
        File file = new File(src);
        if (file.isFile())
            return file.getName();
        else throw new RuntimeException("src is not a valid file");
    }

    private static void transfer(FileChannel from, FileChannel to, long position, long size) throws IOException {
        assert !Objects.isNull(from) && !Objects.isNull(to);

        while (position < size)
            position += from.transferTo(position, Constants.TRANSFER_MAX_SIZE, to);
    }
}
