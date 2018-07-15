public class SlaveFrameTest {
    public static void main(String[] args) {
        new SlaveFrameTest();
    }

    public SlaveFrameTest() {
        SlaveFrame slaveFrame = new SlaveFrame("localhost");
        slaveFrame.setCurrentJobFileName("TestFile.mkv");
        double progress = 0;
        while (true) {
            try {
                Thread.sleep(1000);
                slaveFrame.updateCurrentJob(progress);
                progress += 0.5;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
