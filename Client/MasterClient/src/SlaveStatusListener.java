import java.util.ArrayList;

public interface SlaveStatusListener {
    void onSlaveStatusChanged(ArrayList<String> onlineSlaves);
}
