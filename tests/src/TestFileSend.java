import ruben.distributed_transcoding.FileHandler.FileSender;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class TestFileSend {
    public static void main(String[] args) throws IOException {
        int port = 9001;

        File file = new File("./Resources/testFiles/test.mp4");
        FileSender sender = new FileSender(file.length(), file.getPath(), port, InetAddress.getLocalHost().getHostAddress());
        new Thread(sender).start();
    }
}
