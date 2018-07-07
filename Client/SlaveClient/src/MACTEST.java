import java.io.IOException;

public class MACTEST {
    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().exec("/usr/bin/open -a Terminal -e ./Resources/Mac/ffmpeg");
    }
}
