import java.io.IOException;
import java.io.ObjectInputStream;

public class ListenerService implements Runnable {
    private boolean isConnected = true;

    private ObjectInputStream fromServer;

    public ListenerService(ObjectInputStream fromServer) {
        this.fromServer = fromServer;
    }

    @Override
    public void run() {
        System.out.println("Updater service is running.");
        while (isConnected) {
            //Update Listeners
            try {
                byte dataType = fromServer.readByte();
                switch (dataType) {
                    case 0:
                        //Update something
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
