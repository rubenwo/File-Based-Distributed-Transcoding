package ruben.distributed_transcoding.Server;

import ruben.distributed_transcoding.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class JobDistributingManager {
    private static JobDistributingManager instance = null;

    public static JobDistributingManager getInstance() {
        if (instance == null)
            throw new IllegalStateException("Manager was never initialized!");
        return instance;
    }

    private ArrayList<ConnectionHandler> onlineSlaves = null;
    private ArrayList<String> inputs = new ArrayList<>();
    private String command;

    public JobDistributingManager(ArrayList<ConnectionHandler> onlineSlaves) {
        this.onlineSlaves = onlineSlaves;
        instance = this;
    }

    public void setInputs(String[] inputs) {
        Collections.addAll(this.inputs, inputs);
        setOutputPath(inputs[0]);
    }

    private void setOutputPath(String input) {
        Path outputPath = Paths.get(new File(input).getParent() + "/transcoded");
        try {
            if (!Files.exists(outputPath))
                Files.createDirectory(outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (ConnectionHandler slave : onlineSlaves)
            slave.setOutputDir(outputPath);
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void distributeJobs() {
        if (onlineSlaves.size() != 0) {
            if (inputs.size() != 0) {
                for (ConnectionHandler slave : onlineSlaves) {
                    if (slave.getStatus().equals(StatusEnum.IDLE)) {
                        slave.setStatus(StatusEnum.IN_FILE_TRANSFER);
                        distribute(slave, inputs.get(0), command);
                        inputs.remove(0);
                    }
                    if (inputs.size() == 0) break;
                }
            } else {
                System.out.println("There are no (more) inputs to be transcoded!");
                System.out.println("Resetting PORTS_INDEX to 0");
                Constants.PORTS_INDEX = 0;
            }
        } else {
            System.err.println("There are no slaves online at the moment!");
        }
    }

    private void distribute(ConnectionHandler slave, String filename, String command) {
        slave.sendCommandToSlave(command);
        slave.sendFile(filename);
        slave.setStatus(StatusEnum.ENCODING);
    }

    public void updateOnlineSlavesList(ArrayList<ConnectionHandler> onlineSlaves) {
        this.onlineSlaves = onlineSlaves;
    }
}
