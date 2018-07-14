import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerListenerService implements Runnable {
    private ObjectInputStream fromClient;
    private boolean isConnected = true;

    public ServerListenerService(ConnectionHandler connectionHandler) {
        this.fromClient = connectionHandler.getFromClient();
        System.out.println("Starting Listener updater service...");
    }

    @Override
    public void run() {
        System.out.println("Running Listener updater service.");
        while (isConnected) {
            try {
                byte dataType = fromClient.readByte();
                switch (dataType) {
                    case 0:
                        try {
                            String[] ffmpegCommands = (String[]) fromClient.readObject();
                            for (String command : ffmpegCommands)
                                System.out.println(command);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        double progress = fromClient.readDouble();
                        System.out.println(progress);
                        break;
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
