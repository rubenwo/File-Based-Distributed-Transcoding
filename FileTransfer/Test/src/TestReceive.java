import java.io.File;
import java.io.IOException;

public class TestReceive extends Thread{
    FileReceiver receiver;
    public TestReceive() {
        try {
            File file = new File("./Resources/testFiles/test.mp4");

            receiver = new FileReceiver(9000, new FileWriter("./Resources/testFiles/test4.mp4"), file.length());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            receiver.receive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
