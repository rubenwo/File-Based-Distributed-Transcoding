package ruben.distributed_transcoding.Server;

public interface FFmpegCommandListener {
    void onCommandsReceived(String[] commands);
}
