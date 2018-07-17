import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TempDirTests {
    public TempDirTests() {
        try {
            createTempFile();
            createTempDir();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTempFile() throws IOException {
        Path tempFile = Files.createTempFile("tempfiles", ".tmp");
        List<String> lines = Arrays.asList("Line1", "Line2");
        Files.write(tempFile, lines, Charset.defaultCharset(), StandardOpenOption.WRITE);

        System.out.printf("Wrote text to temporary file %s%n", tempFile.toString());
    }

    private void createTempDir() throws IOException {
        Path tempDir = Files.createTempDirectory(UUID.randomUUID().toString());

        File file = new File(tempDir.toString() + "/tempfile.mkv");
        System.out.println(tempDir.toAbsolutePath());
        System.out.println(file.getAbsolutePath());
    }

    public static void main(String[] args) {
        new TempDirTests();
    }
}
