import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MasterClientListenerService implements Runnable {
    private boolean isConnected = true;
    private ObjectInputStream fromServer;

    private SlaveStatusListener slaveStatusListener;

    public MasterClientListenerService(ObjectInputStream fromServer, SlaveStatusListener slaveStatusListener) {
        this.fromServer = fromServer;
        this.slaveStatusListener = slaveStatusListener;
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
                        try {
                            String[] onlineSlavesArray = (String[]) fromServer.readObject();
                            ArrayList<String> onlineSlaves = new ArrayList<>();
                            for (String onlineSlave : onlineSlavesArray)
                                onlineSlaves.add(onlineSlave);
                            slaveStatusListener.onSlaveStatusChanged(onlineSlaves);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        double progress = fromServer.readDouble();
                        slaveStatusListener.onSlaveProgressRequestAvailable(progress);
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

