
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class FFmpegOptionPanel extends JPanel {
    private HashMap<String, String> ffmpegCommandMap = new HashMap<>();
    private int crf_Min = 0;
    private int crf_Max = 30;
    private int threads_Min = 1;
    private int threads_Max = Runtime.getRuntime().availableProcessors() + 1;
    private String[][] videoPresets = {{"Select a Video Preset", "ultrafast", "superfast", "veryfast", "faster", "fast", "medium", "slow", "slower", "veryslow", "placebo"},
            {"Select a Video Preset", "hq", "hp", "bd", "ll", "llhq", "llhp", "default"}};
    private String[][] audioBitrate = {{"Select Audio Bitrate", "96", "128", "160", "192", "256", "320"}, {"Select Audo Bitrate"}};
    private String[] videoEncoderLibs = {"Select a Video Encoder", "libx264", "libx265", "nvenc_hevc", "nvenc_h264"};
    private String[] audioEncoderLibs = {"Select an Audio Encoder", "mp3", "aac", "ac3", "eac3"};
    private String[] audioChannels = {"Select # of Audio channels (Default = input)", "2.1", "5.1", "7.1"};


    public FFmpegOptionPanel() {
        this.setLayout(new BorderLayout());
        this.add(dropDownPanel(), BorderLayout.CENTER);
    }

    private JPanel dropDownPanel() {
        JPanel dropDownPanel = new JPanel();

        JComboBox<String> videoEncoderLibsBox = new JComboBox<>(videoEncoderLibs);
        JComboBox<String> videoPresetsBox = new JComboBox<>(videoPresets[0]);

        JComboBox<String> crfBox = new JComboBox<>();
        crfBox.addItem("Select CRF");
        for (int crf = crf_Min; crf < crf_Max; crf++)
            crfBox.addItem(Integer.toString(crf));

        JComboBox<String> audioEncoderLibsBox = new JComboBox<>(audioEncoderLibs);
        JComboBox<String> audioChannelsBox = new JComboBox<>(audioChannels);
        JComboBox<String> audioBitrateBox = new JComboBox<>(audioBitrate[0]);

        JComboBox<String> encoderThreadsBox = new JComboBox<>();
        encoderThreadsBox.addItem("Select # of Threads (Default = ALL)");
        for (int thread = threads_Min; thread < threads_Max; thread++)
            encoderThreadsBox.addItem(Integer.toString(thread));

        videoEncoderLibsBox.addActionListener(e -> {
            System.out.println(videoEncoderLibsBox.getSelectedItem());
            if (videoEncoderLibsBox.getSelectedItem().equals("libx264") || videoEncoderLibsBox.getSelectedItem().equals("libx265")) {
                videoPresetsBox.removeAllItems();
                for (int i = 0; i < videoPresets[0].length; i++)
                    videoPresetsBox.addItem(videoPresets[0][i]);

            } else if (videoEncoderLibsBox.getSelectedItem().equals("nvenc_hevc") || videoEncoderLibsBox.getSelectedItem().equals("nvenc_h264")) {
                videoPresetsBox.removeAllItems();
                for (int i = 0; i < videoPresets[1].length; i++)
                    videoPresetsBox.addItem(videoPresets[1][i]);
            }
        });
        videoPresetsBox.addActionListener(e -> {
            System.out.println(videoPresetsBox.getSelectedItem());
        });
        crfBox.addActionListener(e -> {
            System.out.println(crfBox.getSelectedItem());
        });
        audioEncoderLibsBox.addActionListener(e -> {
            System.out.println(audioEncoderLibsBox.getSelectedItem());
            if (audioEncoderLibsBox.getSelectedItem().equals("mp3")) {
                audioBitrateBox.removeAllItems();
                for (int i = 0; i < audioBitrate[0].length; i++)
                    audioBitrateBox.addItem(audioBitrate[0][i]);
            } else if (audioEncoderLibsBox.getSelectedItem().equals("ac3") || audioEncoderLibsBox.getSelectedItem().equals("eac3")) {
                audioBitrateBox.removeAllItems();
                for (int i = 0; i < audioBitrate[1].length; i++)
                    audioBitrateBox.addItem(audioBitrate[1][i]);
            }
        });
        audioChannelsBox.addActionListener(e -> {
            System.out.println(audioChannelsBox.getSelectedItem());
        });
        audioBitrateBox.addActionListener(e -> {
            System.out.println(audioBitrateBox.getSelectedItem());
        });
        encoderThreadsBox.addActionListener(e -> {
            System.out.println(encoderThreadsBox.getSelectedItem());
        });

        dropDownPanel.add(videoEncoderLibsBox);
        dropDownPanel.add(videoPresetsBox);
        dropDownPanel.add(crfBox);
        dropDownPanel.add(audioEncoderLibsBox);
        dropDownPanel.add(audioChannelsBox);
        dropDownPanel.add(audioBitrateBox);
        dropDownPanel.add(encoderThreadsBox);
        return dropDownPanel;
    }

    private void buildDefaultCommandMap() {
        ffmpegCommandMap.put("videoEncoder", "libx264");
        ffmpegCommandMap.put("videoPreset", "medium");
        ffmpegCommandMap.put("audioEncoder", "eac3");
        ffmpegCommandMap.put("audioBitrate", "640");
    }

    private String[] getFFmpegCommands(String[] input) {
        String[] commands = new String[input.length];
        for (int i = 0; i < input.length; i++)
            commands[i] = " -i " + input[i] + " -c:v " + ffmpegCommandMap.get("videoEncoder") + " -preset:v " + ffmpegCommandMap.get("videoPreset") + " -c:a " + ffmpegCommandMap.get("audioEncoder") + " -q:a " + ffmpegCommandMap.get("audioBitrate");
        return commands;
    }
}