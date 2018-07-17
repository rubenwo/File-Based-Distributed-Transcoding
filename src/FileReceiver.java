import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class FileReceiver {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public FileReceiver(int port) {
        try {
            socket = new Socket("192.168.2.41", port);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.flush();
            socket.setReceiveBufferSize(64 * 1024);
            socket.setSendBufferSize(64 * 1024);

            receive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createTempDir() throws IOException {
        Path tempDir = Files.createTempDirectory(UUID.randomUUID().toString());
        return tempDir.toString() + "/";
    }

    private String getFileExtension(String filename) {
        try {
            return filename.substring(filename.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    private void receive() throws IOException {
        String path = createTempDir();
        System.out.println(path);

        String filename = inputStream.readUTF();
        System.out.println("Filename: " + filename);
        Long fileSize = inputStream.readLong();
        System.out.println("File size: " + fileSize + "B");

        long chunks = inputStream.readLong();
        System.out.println("Chunks: " + chunks);

        String fileExtension = getFileExtension(filename);

        String input = path + "Untranscoded." + fileExtension;
        String output = path + "Transcoded.mkv";

        byte[] buffer = new byte[1024];
        System.out.println("Receiving file...");
        FileOutputStream fileOutputStream = new FileOutputStream(new File(input), true);
        long transferStart = System.currentTimeMillis();

        for (int i = 0; i < chunks; i++) {
            fileOutputStream.write(buffer, 0, buffer.length);
        }

        long transferEnd = System.currentTimeMillis();
        double elapsedTimeInSeconds = (transferEnd - transferStart) * 1000;
        double transferSpeed = fileSize / elapsedTimeInSeconds;
        System.out.println(transferSpeed + " MB/s");
        System.out.println("Finished receiving " + filename);
        fileOutputStream.close();
    }

    public static void main(String[] args) {
        new FileReceiver(9100);
    }
}
