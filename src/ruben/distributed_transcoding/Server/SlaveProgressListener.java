package ruben.distributed_transcoding.Server;

public interface SlaveProgressListener {
    void onSlaveProgressAvailable(String slaveId, double progress);
}
