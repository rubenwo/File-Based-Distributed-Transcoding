import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Frame {
    private String[] inputs;
    private ArrayList<String> onlineClients = new ArrayList<>();

    public Frame(HashMap<String, String> slavesMap) {
        onlineClients.addAll(slavesMap.keySet());

        JFrame frame = new JFrame("File-Based Distributed Transcoding Master");
        frame.setPreferredSize(new Dimension(1280, 720));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());

        JPanel onlineClientPanel = new OnlineClientPanel(onlineClients);
        onlineClientPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

        JPanel ffmpegOptionPanel = new FFmpegOptionPanel(this);
        ffmpegOptionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

        contentPane.add(onlineClientPanel, BorderLayout.EAST);
        contentPane.add(ffmpegOptionPanel, BorderLayout.CENTER);

        frame.setJMenuBar(buildMenuBar());
        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");

        JMenuItem openDirectory = new JMenuItem("Open Directory");
        openDirectory.addActionListener(e -> {
            System.out.println("Clicked open.");
            inputs = getInputs(".\\Resources\\testFiles");
        });
        openDirectory.setToolTipText("Choose the directory you wish to transcode.");
        JMenuItem savePreset = new JMenuItem("Save Preset");
        savePreset.addActionListener(e -> {
            System.out.println("Clicked save preset");
        });
        JMenuItem loadPreset = new JMenuItem("Load Preset");
        loadPreset.addActionListener(e -> {
            System.out.println("Clicked load preset");
        });

        file.add(openDirectory);
        file.add(savePreset);
        file.add(loadPreset);

        menuBar.add(file);
        return menuBar;
    }

    private String[] getInputs(String path) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        String[] inputs = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile())
                inputs[i] = listOfFiles[i].getAbsolutePath();
        }
        return inputs;
    }

    public String[] getInputs() {
        return inputs;
    }
}
