package ruben.distributed_transcoding.SlaveClient.CLI;

import ruben.distributed_transcoding.SlaveClient.SlaveClient;

import java.util.Scanner;

public class CommandLineInterface {
    private SlaveClient slaveClient;

    public CommandLineInterface() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please Enter the Server IP-Address.");

        String serverIP = scanner.nextLine();

        slaveClient = new SlaveClient(serverIP, this);
    }

    public static void main(String[] args) {
        new CommandLineInterface();
    }
}
