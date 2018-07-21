package ruben.distributed_transcoding.Server.ChunksHandler;

import ruben.distributed_transcoding.SlaveClient.ProgressListener;

import java.io.IOException;
import java.util.ArrayList;

public class ChunkConcatenator implements ProgressListener {
    private Process ffmpeg = null;
    private String chunkFilePath;

    public ChunkConcatenator(String chunkFilePath) {
        this.chunkFilePath = chunkFilePath;
        ArrayList<String> commands = new ArrayList<>();

        //ffmpeg -f concat -safe 0 -i mylist.txt -c copy output

        try {
            ffmpeg = new ProcessBuilder().redirectErrorStream(true).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onJobSubmitted(String fileName) {

    }

    @Override
    public void onProgressUpdate(double progress) {

    }

    @Override
    public void onJobDone() {

    }

    @Override
    public void onError(Error error) {

    }
}
