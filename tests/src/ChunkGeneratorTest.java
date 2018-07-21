import ruben.distributed_transcoding.Server.ChunksHandler.ChunkGenerator;
import ruben.distributed_transcoding.Utils.FFmpegHandler;
import ruben.distributed_transcoding.Utils.OperatingSystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class ChunkGeneratorTest {
    private String ID = UUID.randomUUID().toString();
    private String tempDir;
    private OperatingSystem operatingSystem = OperatingSystem.detectOperatingSystem();

    public ChunkGeneratorTest() throws IOException {
        this.tempDir = createTempDir();
        createEncoder();

        new ChunkGenerator(tempDir + "ffmpeg", "/res/test_files/big_buck_bunny.mp4", tempDir, operatingSystem);
    }

    private String createTempDir() throws IOException {
        Path tempDir = Files.createTempDirectory(this.ID);
        return tempDir.toString() + "/";
    }

    private void createEncoder() throws IOException {
        String tempEncoder = tempDir + "ffmpeg";
        long start = System.currentTimeMillis();

        InputStream in = getClass().getResourceAsStream(OperatingSystem.getEncoderPath(operatingSystem));
        Path tempEncoderPath = Paths.get(tempEncoder);
        Files.copy(in, tempEncoderPath);
        in.close();
        System.out.println(tempEncoder);
        new File(tempEncoder).setExecutable(true);

        long end = System.currentTimeMillis();
        System.out.println("Extracted in: " + (end - start) + " milliseconds");
    }


    public static void main(String[] args) throws IOException {
        new ChunkGeneratorTest();
    }
}

