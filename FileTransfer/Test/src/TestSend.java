import java.io.File;
import java.io.IOException;

public class TestSend extends Thread {
    FileReader fileReader;
    public TestSend() {
        try {
            File file = new File("./Resources/testFiles/test.mp4");

            FileSender sender = new FileSender(9000);

            fileReader  = new FileReader(sender, file.getPath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            fileReader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
