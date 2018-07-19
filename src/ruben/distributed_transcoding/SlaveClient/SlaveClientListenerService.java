package ruben.distributed_transcoding.SlaveClient;

import java.io.IOException;
import java.io.ObjectInputStream;

public class SlaveClientListenerService extends Thread {

    private boolean isConnected = true;
    private ObjectInputStream fromServer;
    private FFmpegJobRequestListener fFmpegJobRequestListener;
    private SlaveClient slaveClient;

    public SlaveClientListenerService(SlaveClient slaveClient, FFmpegJobRequestListener fFmpegJobRequestListener) {
        this.slaveClient = slaveClient;
        this.fromServer = slaveClient.getFromServer();
        this.fFmpegJobRequestListener = fFmpegJobRequestListener;
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
                        fFmpegJobRequestListener.onJobRequest(command);
                        break;
                    case 3:
                        slaveClient.receiveFile();
                        break;
                    case 4:
                        slaveClient.startFileSender();
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
