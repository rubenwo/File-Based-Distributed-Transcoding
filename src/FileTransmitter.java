import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransmitter {
    private ServerSocket serverSocket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public FileTransmitter(String fileName, int port) {
        try {
            serverSocket = new ServerSocket(port, 0, InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()));
            Socket socket = serverSocket.accept();

            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream.flush();

            transmit(socket, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transmit(Socket socket, String filename) throws IOException {
        System.out.println("Sending File: " + filename);
        outputStream.writeUTF(filename);
        outputStream.flush();

        File file = new File(filename);
        FileInputStream fileInputStream = new FileInputStream(file);

        long fileSize = file.length();

        byte[] buffer = new byte[1024];

        int read;

        outputStream.writeLong(fileSize);
        outputStream.flush();

        System.out.println("File size: " + fileSize + "B");
        System.out.println("Buffer size: " + socket.getReceiveBufferSize());

        while ((read = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
            outputStream.flush();
        }

        fileInputStream.close();
        outputStream.flush();
        System.out.println("Finished sending: " + filename);
    }


    public static void main(String[] args) {
        new FileTransmitter("./Resources/testFiles/test.mp4", 9100);
    }
}
