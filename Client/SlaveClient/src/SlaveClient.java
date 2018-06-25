import java.io.IOException;
import java.net.Socket;

public class SlaveClient {
    private Socket socket;

    public SlaveClient() {
        openSocket();
    }

    private void openSocket() {
        try {
            socket = new Socket(MasterClient.HOSTNAME, MasterClient.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SlaveClient();
    }
}
