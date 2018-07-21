package ruben.distributed_transcoding.Server.ChunksHandler;

import ruben.distributed_transcoding.SlaveClient.ProgressListener;
import ruben.distributed_transcoding.Utils.FFmpegHandler;
import ruben.distributed_transcoding.Utils.OperatingSystem;

import java.io.File;

public class ChunkGenerator implements ProgressListener {
    private String outputPath;

    public ChunkGenerator(String encoderPath, String inputPath, String outputPath, OperatingSystem operatingSystem) {
        this.outputPath = outputPath;
        String generateChunksCommand = "-c copy -f segment -segment_time 120";
        new File(outputPath).mkdir();
        this.outputPath = outputPath + "chunk_ % 03d.mp4";

        new Thread(new FFmpegHandler(encoderPath, inputPath, generateChunksCommand, outputPath, this)).start();
    }

    @Override
    public void onJobSubmitted(String fileName) {
        System.out.println("Began generating chunks for: " + fileName);
    }

    @Override
    public void onProgressUpdate(double progress) {
        System.out.println("Generating chunks: " + progress + "% done.");
    }

    @Override
    public void onJobDone() {
        System.out.println("Done generating chunks!");
    }

    @Override
    public void onError(Error error) {
        throw error;
    }
}
