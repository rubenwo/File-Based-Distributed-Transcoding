import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TestFileReceive {
    public static void main(String[] args) throws IOException {
        int port = 9001;

        File file = new File("./Resources/testFiles/test.mp4");
        FileReceiver receiver = new FileReceiver(UUID.randomUUID().toString(), file.length(), port, ".mp4", ".mov");

        new Thread(receiver).start();
    }
}
