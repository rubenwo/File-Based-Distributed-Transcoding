import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TestFileReceive implements FileReceiverListener {
    public static void main(String[] args) throws IOException {
        new TestFileReceive();
    }

    public TestFileReceive() throws IOException {
        int port = 9001;

        File file = new File("./Resources/testFiles/test.mp4");
        FileReceiver receiver = new FileReceiver(UUID.randomUUID().toString(), file.length(), port, ".mp4", ".mov", this);

        new Thread(receiver).start();
    }

    @Override
    public void onSocketBound() {

    }

    @Override
    public void onFileReceived(String input, String output) {
        System.out.println("File Received!");
    }
}
