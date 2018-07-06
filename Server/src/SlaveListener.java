public interface SlaveListener {
    void onSlaveOnline(String slaveClient);

    void onSlaveOffline(String slaveClient);

    void onError();
}
