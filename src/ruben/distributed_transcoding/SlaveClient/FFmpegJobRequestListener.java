package ruben.distributed_transcoding.SlaveClient;

public interface FFmpegJobRequestListener {
    void onJobRequest(String command);
}
