package ruben.distributed_transcoding;

public class Constants {
    public static final int PORT = 9000;
    public static int[] PORTS;
    public static int PORTS_INDEX = 0;

    public synchronized static void INCREASE_PORTS_INDEX() {
        PORTS_INDEX++;
    }
}
