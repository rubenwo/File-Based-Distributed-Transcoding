import java.io.*;
import java.net.Socket;

public class FileReceiver {
    public static void main(String[] args) throws Exception {

        long start = System.currentTimeMillis();

        // localhost for testing
        Socket sock = new Socket("127.0.0.1", 13267);
        System.out.println("Connecting...");
        InputStream is = sock.getInputStream();
        // receive file
        new FileReceiver().receiveFile(is);
        OutputStream os = sock.getOutputStream();
        //new FileClient().send(os);
        long end = System.currentTimeMillis();
        System.out.println(end - start);

        sock.close();
    }


    public void send(OutputStream os) throws Exception {
        // sendfile
        File myFile = new File("/home/nilesh/opt/eclipse/about.html");
        byte[] mybytearray = new byte[(int) myFile.length() + 1];
        FileInputStream fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(mybytearray, 0, mybytearray.length);
        System.out.println("Sending...");
        os.write(mybytearray, 0, mybytearray.length);
        os.flush();
    }

    public void receiveFile(InputStream is) throws Exception {
        int filesize = 87471087;
        int bytesRead;
        int current = 0;
        byte[] mybytearray = new byte[filesize];
        FileOutputStream fos = new FileOutputStream("./Resources/test.mp4");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bytesRead = is.read(mybytearray, 0, mybytearray.length);
        current = bytesRead;

        do {
            bytesRead = is.read(mybytearray, current,
                    (mybytearray.length - current));
            if (bytesRead >= 0)
                current += bytesRead;
        } while (bytesRead > -1);

        bos.write(mybytearray, 0, current);
        bos.flush();
        bos.close();
    }
}
