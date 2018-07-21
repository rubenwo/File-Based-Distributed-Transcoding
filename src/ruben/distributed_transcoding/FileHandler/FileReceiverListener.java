package ruben.distributed_transcoding.FileHandler;

public interface FileReceiverListener {
    void onSocketBound();

    void onFileReceived(String input, String output);
}
