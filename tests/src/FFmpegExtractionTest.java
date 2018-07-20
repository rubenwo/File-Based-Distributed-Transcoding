import ruben.distributed_transcoding.SlaveClient.FFmpegHandler;
import ruben.distributed_transcoding.SlaveClient.OperatingSystem;
import ruben.distributed_transcoding.SlaveClient.ProgressListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FFmpegExtractionTest implements ProgressListener {
    private String ID = UUID.randomUUID().toString();
    private OperatingSystem operatingSystem = OperatingSystem.detectOperatingSystem();
    private String tempDir;

    public FFmpegExtractionTest() throws IOException {
        tempDir = createTempDir();
        createEncoder();

        String command = "-c:v libx264 -preset:v ultrafast -c:a copy";
        startEncoding((tempDir + "ffmpeg"), "/test_files/big_buck_bunny.mp4", command, "C:/Users/ruben woldhuis/Downloads/transcoded/test.mp4");
    }

    private void startEncoding(String encoderPath, String input, String command, String output) {
        new Thread(new FFmpegHandler(encoderPath, input, command, output, this)).start();
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

    @Override
    public void onJobSubmitted(String fileName) {
        System.out.println("Transcoding: " + fileName);
    }

    @Override
    public void onProgressUpdate(double progress) {
        System.out.println("Progress: " + progress + "%");
    }

    @Override
    public void onJobDone() {
        System.out.println("Done Transcoding!");
    }

    @Override
    public void onError(Error error) {
        throw error;
    }

    public static void main(String[] args) throws IOException {
        new FFmpegExtractionTest();
    }
}
