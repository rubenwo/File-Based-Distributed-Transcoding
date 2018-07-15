public class FFmpegHandlerTest implements ProgressListener {
    public static void main(String[] args) {
        new FFmpegHandlerTest();
    }

    private SlaveFrame slaveFrame;

    public FFmpegHandlerTest() {
        OperatingSystem operatingSystem = OperatingSystem.detectOperatingSystem();

        String command = "-i ./Resources/testFiles/test.mp4 -c:v libx264 -preset:v ultrafast -c:a copy ./Resources/test.mkv";

        FFmpegHandler fFmpegHandler = new FFmpegHandler(operatingSystem, command, this);
        new Thread(fFmpegHandler).start();
        slaveFrame = new SlaveFrame("localhost");
    }

    @Override
    public void onProgressUpdate(String fileName, double progress) {
        System.out.println(progress);
        slaveFrame.updateCurrentJob(fileName, progress);
    }

    @Override
    public void onJobDone() {
        System.out.println("Done transcoding");
        slaveFrame.resetFrame();
    }

    @Override
    public void onError(Error error) {

    }
}
