public interface ClientStatusListener {
    void onSlaveOnline(ConnectionHandler slaveHandler);

    void onSlaveOffline(ConnectionHandler slaveHandler);

    void onMasterOnline(ConnectionHandler masterHandler);

    void onMasterOffline(ConnectionHandler masterHandler);

    void onError(Error error);
}
