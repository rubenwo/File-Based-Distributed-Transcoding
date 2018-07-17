import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {
    private boolean running = true;

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private Iterator<SelectionKey> selectionKeyIterator;


    public Server() {
        try {
            setupServerSocketChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupServerSocketChannel() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("localhost", 9000));
        serverSocketChannel.configureBlocking(false);

        setupSelector();
    }

    private void setupSelector() throws IOException {
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        selectionKeyIterator = selector.selectedKeys().iterator();

        loop();
    }

    private void loop() throws IOException {
        SelectionKey selKey;
        while (running) {
            selector.select(1000);
            while (selectionKeyIterator.hasNext()) {
                selKey = selectionKeyIterator.next();

                selectionKeyIterator.remove();

                if (selKey.isAcceptable()) {
                    System.out.println("Client connected!");
                    SocketChannel clientChannel = ((ServerSocketChannel) selKey.channel()).accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ, SelectionKey.OP_WRITE);
                    System.out.println("Client set up!");

                    final ByteBuffer byteBuffer = ByteBuffer.wrap("Hello Client!!!".getBytes());
                    byteBuffer.flip();
                    int bytesWritten = 0;
                    for (; ; ) {
                        bytesWritten = clientChannel.write(byteBuffer);
                        if (bytesWritten > 0 && !byteBuffer.hasRemaining()) {
                            break;
                        }
                        byteBuffer.compact();
                    }
                } else if (selKey.isReadable()) {
                    System.out.println("reading client message");
                    ByteBuffer buffer = ByteBuffer.allocate(20);
                    SocketChannel clientChannel = (SocketChannel) selKey.channel();

                    int bytesRead = 0;
                    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] data;

                    for (; ; ) {
                        buffer.clear();
                        bytesRead = clientChannel.read(buffer);
                        if (bytesRead > 0) {
                            buffer.flip();
                            data = new byte[bytesRead];
                            buffer.get(data, 0, bytesRead);
                            byteArrayOutputStream.write(data);
                        } else if (bytesRead == 0) {
                            // the full message has been received, no more bytes on the buffer
                            System.out.println("read message: " + byteArrayOutputStream.toString());
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}