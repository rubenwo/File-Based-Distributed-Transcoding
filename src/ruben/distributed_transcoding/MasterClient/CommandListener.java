package ruben.distributed_transcoding.MasterClient;

public interface CommandListener {
    void onCommandsAvailable(String[] commands);

    void onNoInputSelected();
}
