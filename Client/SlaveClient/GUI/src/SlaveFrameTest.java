public class SlaveFrameTest {
    public static void main(String[] args) {
        new SlaveFrameTest();
    }

    public SlaveFrameTest() {
        SlaveFrame slaveFrame = new SlaveFrame("localhost", "Random ClientId");
        slaveFrame.setCurrentJobFileName("TestFile.mkv");
        double progress = 0;
        while (true) {
            try {
                Thread.sleep(10);
                slaveFrame.updateCurrentJob(progress);
                progress += 0.5;
                if (progress == 100.0)
                    break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        slaveFrame.resetFrame();
    }
}
