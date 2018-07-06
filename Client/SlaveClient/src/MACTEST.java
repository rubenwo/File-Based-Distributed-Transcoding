import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class MACTEST {
    public static void main(String[] args) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/open -a Terminal", "cd ./Resources/Mac", "./ffmpeg -hwaccels");
        processBuilder.start();
    }
}
