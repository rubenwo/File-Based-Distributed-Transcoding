public class FFmpegHandlerTest implements ProgressListener {
    public static void main(String[] args) {
        new FFmpegHandlerTest();
    }

    private SlaveFrame slaveFrame;

    public FFmpegHandlerTest() {
        OperatingSystem operatingSystem = OperatingSystem.detectOperatingSystem();
        slaveFrame = new SlaveFrame("localhost");

        String command = "-i ./Resources/testFiles/test.mp4 -c:v libx264 -preset:v ultrafast -tune film -c:a copy ./Resources/test.mkv";

        FFmpegHandler fFmpegHandler = new FFmpegHandler(operatingSystem, command, this);
        new Thread(fFmpegHandler).start();
    }


    @Override
    public void onJobSubmitted(String fileName) {
        slaveFrame.setCurrentJobFileName(fileName);
    }

    @Override
    public void onProgressUpdate(double progress) {
        slaveFrame.updateCurrentJob(progress);
    }

    @Override
    public void onJobDone() {
        System.out.println("Done transcoding!");
        slaveFrame.resetFrame();
    }

    @Override
    public void onError(Error error) {

    }
}
