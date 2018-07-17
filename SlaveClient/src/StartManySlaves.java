public class StartManySlaves {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++)
            SlaveClient.main(null);
    }
}
