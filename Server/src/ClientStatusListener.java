public interface ClientStatusListener {
    void onSlaveOnline(String slaveClient, String slaveClientIp);

    void onSlaveOffline(String slaveClient);

    void onMasterOnline(String masterClient, String masterClientIp);

    void onMasterOffline(String masterClient);

    void onError(Error error);
}
