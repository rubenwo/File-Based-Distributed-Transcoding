import java.io.IOException;

public class ServerListenerService implements Runnable {
    private ConnectionHandler connectionHandler;
    private boolean isConnected = true;

    private SlaveProgressListener slaveProgressListener;

    public ServerListenerService(ConnectionHandler connectionHandler, SlaveProgressListener slaveProgressListener) {
        this.connectionHandler = connectionHandler;
        this.slaveProgressListener = slaveProgressListener;
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
                            for (String command : ffmpegCommands)
                                System.out.println(command);
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
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
