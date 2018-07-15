import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MasterClientListenerService implements Runnable {
    private boolean isConnected = true;
    private ObjectInputStream fromServer;
    private ArrayList<String> onlineSlaves = new ArrayList<>();

    public MasterClientListenerService(ObjectInputStream fromServer) {
        this.fromServer = fromServer;
    }

    @Override
    public void run() {
        System.out.println("Updater service is running.");
        while (isConnected) {
            //Update Listeners
            System.out.println("Awaiting update...");
            try {
                byte dataType = fromServer.readByte();
                System.out.println(dataType);
                switch (dataType) {
                    case 2:
                        try {
                            System.out.println(onlineSlaves.size());
                            onlineSlaves = (ArrayList<String>) fromServer.readObject();
                            System.out.println(onlineSlaves.size());
                            for (String slave : onlineSlaves)
                                System.out.println(slave);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
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

