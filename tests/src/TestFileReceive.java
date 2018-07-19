import ruben.distributed_transcoding.FileHandler.FileReceiver;
import ruben.distributed_transcoding.FileHandler.FileReceiverListener;

import java.io.File;
import java.io.IOException;

public class TestFileReceive implements FileReceiverListener {
    public static void main(String[] args) throws IOException {
        new TestFileReceive();
    }

    public TestFileReceive() throws IOException {
        int port = 9001;

        File file = new File("./res/testFiles/test.mp4");
        FileReceiver receiver = new FileReceiver(file.length(), port, ".mp4", ".mov", this, null);

        new Thread(receiver).start();
    }

    @Override
    public void onSocketBound() {

    }

    @Override
    public void onFileReceived(String input, String output) {

    }
}
