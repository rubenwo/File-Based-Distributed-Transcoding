public class Server {
    public static final int PORT = 9000;
    public static final String HOSTNAME = "localhost";

    public static void main(String[] args) {
        new Thread(new ThreadedServer()).start();
        
        /*String osName = System.getProperty("os.name");

        if (osName.contains("Windows")) {
            //.\\Resources\\Windows\\
        } else if (osName.contains("Mac")) {
            // .\\Resources\\Mac OS X\\
        } else if (osName.contains("Linux")) {
            // .\\Resources\\Linux\\
        } else {
            System.out.println("Operating system is not supported");
        }
        */
    }
}
