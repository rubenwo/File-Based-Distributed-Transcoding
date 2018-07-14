import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class FFmpegReaderTest {
    public static void main(String[] args) {
        String ff = OperatingSystem.getEncoderPath(OperatingSystem.WINDOWS);
        String input = "-i";
        String inputPath = "./Resources/testFiles/test.mp4";
        String vCommand = "libx264";
        String aCommand = "copy";
        String output = "./Resources/test2.mkv";
        Process processDuration = null;
        try {
            processDuration = new ProcessBuilder(ff, input, inputPath, "-c:v", vCommand, "-preset:v", "slow", "-c:a", aCommand, output).redirectErrorStream(true).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(processDuration.getInputStream(), Charset.defaultCharset()));
        try {
            String line;
            int durationMillis = 0;

            while ((line = processOutputReader.readLine()) != null) {
                if (line.contains("Duration")) {
                    System.out.println(line);
                    String[] durationString = line.split(",")[0].substring(12, 23).split(":");
                    durationMillis += Integer.valueOf(durationString[0]) * 3600000;
                    durationMillis += Integer.valueOf(durationString[1]) * 60000;
                    durationMillis += Integer.valueOf(durationString[2].substring(0, 2)) * 1000;
                    durationMillis += Integer.valueOf(durationString[2].substring(3, 5));

                    System.out.println(durationMillis);
                }
                if (line.contains("frame") && line.contains("time")) {
                    String[] currentTime = line.split("=")[5].split(":");

                    int currentTimeMillis = 0;
                    currentTimeMillis += Integer.valueOf(currentTime[0]) * 3600000;
                    currentTimeMillis += Integer.valueOf(currentTime[1]) * 60000;
                    currentTimeMillis += Integer.valueOf(currentTime[2].substring(0, 2)) * 1000;
                    currentTimeMillis += Integer.valueOf(currentTime[2].substring(3, 5));

                    double percent = (double) currentTimeMillis / durationMillis * 100;
                    percent = Math.round(percent * 100.0) / 100.0;
                    System.out.println(percent + "% Done");
                }
            }
            processDuration.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
