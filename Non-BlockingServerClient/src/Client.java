import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) throws UnknownHostException,
            IOException, InterruptedException {
        Client client = new Client();
        client.runClient();
    }

    public void runClient() throws UnknownHostException, IOException,
            InterruptedException {
        // setup the socket channel
        SocketChannel channel = SocketChannel.open();

        // we open this channel in non blocking mode
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress("localhost", 9000));

        // wait for it to finish connecting
        while (!channel.finishConnect()) {
            System.out.println("still connecting");
        }

        // since the server writes a message on connect, we read that message
        // from the channel
        System.out.println("reading server message");
        ByteBuffer buffer = ByteBuffer.allocate(20);
        int bytesRead = 0;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] ba = null;
        for (; ; ) {
            buffer.clear();
            bytesRead = channel.read(buffer);
            if (bytesRead > 0) {
                buffer.flip();
                ba = new byte[bytesRead];
                buffer.get(ba, 0, bytesRead);
                baos.write(ba);
            } else if (bytesRead <= 0) {
                System.out.println("read message: " + baos.toString());
                break;
            }
        }

        // next we write 10 messages to the server
        ByteBuffer bbuf = ByteBuffer.wrap("client message\n".getBytes());
        bbuf.flip();
        int bytesWritten = 0;
        for (int runs = 0; runs < 10; runs++) {
            System.out.println("writing client message");
            bytesWritten = channel.write(bbuf);
            if (bytesWritten > 0 && !bbuf.hasRemaining()) {
                bbuf = ByteBuffer.wrap("client message".getBytes());
                bbuf.flip();
            }
            bbuf.compact();
        }

        // and finally we close the connection
        channel.close();
    }

}
