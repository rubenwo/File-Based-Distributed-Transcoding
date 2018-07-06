public class Server {
    public static final int PORT = 9000;
    public static final String HOSTNAME = "localhost";

    public static void main(String[] args) {
        new Thread(new ThreadedServer()).start();
    }
}
