import ruben.distributed_transcoding.Utils.FFmpegHandler;
import ruben.distributed_transcoding.SlaveClient.GUI.SlaveFrame;
import ruben.distributed_transcoding.Utils.OperatingSystem;
import ruben.distributed_transcoding.SlaveClient.ProgressListener;

public class FFmpegHandlerTest implements ProgressListener {
    public static void main(String[] args) {
        new FFmpegHandlerTest();
    }

    private SlaveFrame slaveFrame;

    public FFmpegHandlerTest() {
        OperatingSystem operatingSystem = OperatingSystem.detectOperatingSystem();
        slaveFrame = new SlaveFrame("localhost", "Test clientId");
        String input = "./res/test_files/big_buck_bunny.mp4";
        String command = "-c:v libx264 -preset:v ultrafast -tune film -c:a copy -c:s copy";
        String output = "./res/transcoded_big_buck_bunny.mkv";

        FFmpegHandler fFmpegHandler = new FFmpegHandler("", input, command, output, this);
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
