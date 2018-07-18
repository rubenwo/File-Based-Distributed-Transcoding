import java.io.IOException;

public class ServerListenerService implements Runnable {
    private ConnectionHandler connectionHandler;
    private boolean isConnected = true;

    private SlaveProgressListener slaveProgressListener;
    private FFmpegCommandListener fFmpegCommandListener;
    private JobDistributingManager distributingManager;

    public ServerListenerService(ConnectionHandler connectionHandler, SlaveProgressListener slaveProgressListener, FFmpegCommandListener fFmpegCommandListener) {
        this.connectionHandler = connectionHandler;
        this.slaveProgressListener = slaveProgressListener;
        this.fFmpegCommandListener = fFmpegCommandListener;
        this.distributingManager = JobDistributingManager.getInstance();
        System.out.println("Starting Listener updater service...");
    }

    @Override
    public void run() {
        System.out.println("Running Listener updater service.");
        while (isConnected) {
            try {
                byte dataType = connectionHandler.getFromClient().readByte();
                switch (dataType) {
                    case 0:
                        try {
                            String[] ffmpegCommands = (String[]) connectionHandler.getFromClient().readObject();
                            fFmpegCommandListener.onCommandsReceived(ffmpegCommands);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        double progress = connectionHandler.getFromClient().readDouble();
                        slaveProgressListener.onSlaveProgressAvailable(connectionHandler.getClientId(), progress);
                        break;
                    case 2:
                        String clientId = connectionHandler.getFromClient().readUTF();
                        connectionHandler.sendProgress(clientId);
                        break;
                    case 3:
                        connectionHandler.startFileSenderThread();
                        break;
                    case 4:
                        connectionHandler.setStatus(StatusEnum.IDLE);
                        distributingManager.distributeJobs();
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        isConnected = false;
    }
}
