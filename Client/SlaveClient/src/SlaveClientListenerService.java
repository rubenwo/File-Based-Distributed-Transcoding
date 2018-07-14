import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class SlaveClientListenerService implements Runnable {

    private boolean isConnected = true;
    private ObjectInputStream fromServer;
    private FFmpegJobListener fFmpegJobListener;

    public SlaveClientListenerService(ObjectInputStream fromServer, FFmpegJobListener fFmpegJobListener) {
        this.fromServer = fromServer;
        this.fFmpegJobListener = fFmpegJobListener;
    }

    @Override
    public void run() {
        System.out.println("Updater service is running.");
        while (isConnected) {
            //Update Listeners
            try {
                byte dataType = fromServer.readByte();
                switch (dataType) {
                    case 1:
                        String command = fromServer.readUTF();
                        fFmpegJobListener.onJobRequest(command);
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
