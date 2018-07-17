import java.io.IOException;
import java.util.ArrayList;

public class JobDistributingManager {
    private static JobDistributingManager instance = null;

    public static JobDistributingManager getInstance(ArrayList<ConnectionHandler> onlineSlaves) {
        if (instance == null)
            instance = new JobDistributingManager(onlineSlaves);
        return instance;
    }

    private ArrayList<ConnectionHandler> onlineSlaves;
    private ArrayList<String> inputs = new ArrayList<>();
    private String command;

    private JobDistributingManager(ArrayList<ConnectionHandler> onlineSlaves) {
        this.onlineSlaves = onlineSlaves;
    }

    public void setInputs(String[] inputs) {
        for (String input : inputs)
            this.inputs.add(input);
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void distributeJobs() {
        if (onlineSlaves.size() > 0) {
            for (ConnectionHandler slave : onlineSlaves) {
                if (slave.getStatus().equals(StatusEnum.IDLE)) {
                    slave.setStatus(StatusEnum.IN_FILE_TRANSFER);
                    //dist(slave, inputs.get(0), command);
                    new Thread(new Distributor(slave, inputs.get(0))).start();
                    inputs.remove(0);
                }
                if (inputs.size() == 0) break;
            }
        } else {
            System.out.println("There are no slaves online at the moment!");
        }
    }

    private void dist(ConnectionHandler slave, String filename, String command) {
        slave.sendCommandToSlave(command);
        try {
            slave.sendFile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        slave.setStatus(StatusEnum.ENCODING);
    }

    public void updateOnlineSlavesList(ArrayList<ConnectionHandler> onlineSlaves) {
        this.onlineSlaves = onlineSlaves;
    }

    class Distributor implements Runnable {
        private ConnectionHandler slave;
        private String filename;

        public Distributor(ConnectionHandler slave, String filename) {
            this.slave = slave;
            this.filename = filename;
        }

        @Override
        public void run() {
            System.out.println("Starting transmission Thread...");
            slave.sendCommandToSlave(command);
            try {
                slave.sendFile(filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
            slave.setStatus(StatusEnum.ENCODING);
            System.out.println("Closing transmission Thread!");
        }
    }
}
