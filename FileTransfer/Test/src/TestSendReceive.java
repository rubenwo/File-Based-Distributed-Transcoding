public class TestSendReceive {
    public static void main(String[] args) {
        new TestReceive().start();
        new TestSend().start();
    }
}
