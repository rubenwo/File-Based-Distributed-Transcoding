public class FFmpegHandlerTest implements ProgressListener {
    public static void main(String[] args) {
        new FFmpegHandlerTest();
    }

    public FFmpegHandlerTest() {
        OperatingSystem operatingSystem = OperatingSystem.detectOperatingSystem();

        String command = "-i ./Resources/testFiles/test.mp4 -c:v libx264 -preset:v ultrafast -c:a copy ./Resources/test.mkv";
        
        FFmpegHandler fFmpegHandler = new FFmpegHandler(operatingSystem, command, this);
        new Thread(fFmpegHandler).start();
    }

    @Override
    public void onProgressUpdate(double progress) {
        System.out.println(progress);
    }

    @Override
    public void onError(Error error) {

    }
}
