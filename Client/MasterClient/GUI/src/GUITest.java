import java.util.ArrayList;

public class GUITest implements CommandListener {
    public static void main(String[] args) {
        new GUITest();
    }

    OperatingSystem operatingSystem;

    public GUITest() {
        operatingSystem = OperatingSystem.WINDOWS;
        ArrayList<String> onlineClients = new ArrayList<>();
        onlineClients.add("Client 1");
        onlineClients.add("Client 2");
        onlineClients.add("Client 3");
        onlineClients.add("Client 4");
        onlineClients.add("Client 5");

        new Frame(onlineClients, this);
    }

    @Override
    public void onCommandsAvailable(String[] commands) {
        String[] ffmpegCommands = new String[commands.length];
        for (int i = 0; i < commands.length; i++)
            ffmpegCommands[i] = OperatingSystem.getEncoderPath(operatingSystem) + commands[i];
    }

    @Override
    public void onNoInputSelected() {
        System.out.println("No input selected!");
    }
}
