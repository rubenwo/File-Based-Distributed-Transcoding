import java.io.IOException;

public class TestCopy {
    public static void main(String[] args) {
        try {
            FileCopy.copy("./Resources/testFiles/test.mp4", "./Resources");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
