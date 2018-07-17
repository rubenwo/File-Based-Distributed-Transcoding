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
            String host = InetAddress.getLocalHost().getHostAddress();
            System.out.println(host);

            serverSocket = new ServerSocket(port, 0, InetAddress.getByName(host));
            Socket socket = serverSocket.accept();

            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream.flush();
            socket.setSendBufferSize(1024 * 64);
            socket.setReceiveBufferSize(1024 * 64);


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
        
        long length = file.length();
        byte[] bytes = new byte[16 * 1024];
        InputStream in = new FileInputStream(file);
        OutputStream out = socket.getOutputStream();

        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        fileInputStream.close();
        outputStream.flush();
        System.out.println("Finished sending: " + filename);
    }


    public static void main(String[] args) {
        new FileTransmitter("./Resources/testFiles/test.mp4", 9100);
    }
}
