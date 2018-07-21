package ruben.distributed_transcoding.MasterClient;

import java.util.ArrayList;

public interface SlaveStatusListener {
    void onSlaveStatusChanged(ArrayList<String> onlineSlaves);

    void onSlaveProgressRequest(String slaveId);

    void onSlaveProgressRequestAvailable(double progress);
}
