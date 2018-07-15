public class SlaveFrameTest {
    public static void main(String[] args) {
        new SlaveFrameTest();
    }

    public SlaveFrameTest() {
        SlaveFrame slaveFrame = new SlaveFrame("localhost");
        double progress = 0;
        while (true) {
            try {
                Thread.sleep(300);
                slaveFrame.updateCurrentJob("Test File.mkv", progress);
                progress += 0.5;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
