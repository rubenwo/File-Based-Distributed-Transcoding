import java.util.ArrayList;

public class GUITest implements CommandListener, SlaveStatusListener {
    public static void main(String[] args) {
        new GUITest();
    }

    OperatingSystem operatingSystem;
    private MasterFrame masterFrame;

    public GUITest() {
        operatingSystem = OperatingSystem.WINDOWS;
        ArrayList<String> onlineClients = new ArrayList<>();
        onlineClients.add("Client 1");
        onlineClients.add("Client 2");
        onlineClients.add("Client 3");
        onlineClients.add("Client 4");
        onlineClients.add("Client 5");

        masterFrame = new MasterFrame(onlineClients, this, this);
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

    @Override
    public void onSlaveStatusChanged(ArrayList<String> onlineSlaves) {

    }

    @Override
    public void onSlaveProgressRequest(String slaveId) {

    }

    @Override
    public void onSlaveProgressRequestAvailable(double progress) {
        masterFrame.updateProgressBar(progress);
    }
}
