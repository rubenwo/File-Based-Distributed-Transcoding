import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerListenerService implements Runnable {
    private ObjectInputStream fromClient;
    private boolean isConnected = true;

    public ServerListenerService(ObjectInputStream fromClient) {
        this.fromClient = fromClient;
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
                            for (int i = 0; i < ffmpegCommands.length; i++)
                                System.out.println(ffmpegCommands[i]);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
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
