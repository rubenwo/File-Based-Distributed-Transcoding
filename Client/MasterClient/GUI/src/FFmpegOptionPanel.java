import sun.tools.jps.Jps;

import javax.swing.*;
import java.awt.*;

public class FFmpegOptionPanel extends JPanel {
    private int crf_Min = 0;
    private int crf_Max = 30;
    private int threads_Min = 1;
    private int threads_Max = Runtime.getRuntime().availableProcessors() + 1;
    private String[][] videoPresets = {{"Select a Video Preset", "ultrafast", "superfast", "veryfast", "faster", "fast", "medium", "slow", "slower", "veryslow", "placebo"}, {"Select a Video Preset"}};
    private String[][] audioPresets = {{"Select an Audio Preset", ""}, {"Select an Audio Preset"}};
    private String[] videoEncoderLibs = {"Select a Video Encoder", "libx264", "libx265", "nvenc_hevc", "nvenc_h264"};
    private String[] audioEncoderLibs = {"Select an Audio Encoder", "aac", "ac3"};

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
        JComboBox<String> audioPresetsBox = new JComboBox<>(audioPresets[0]);

        JComboBox<String> encoderThreadsBox = new JComboBox<>();
        encoderThreadsBox.addItem("Select # of Threads");
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
                    videoPresetsBox.addItem(videoPresets[0][i]);
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
        });
        audioPresetsBox.addActionListener(e -> {
            System.out.println(audioPresetsBox.getSelectedItem());
        });
        encoderThreadsBox.addActionListener(e -> {
            System.out.println(encoderThreadsBox.getSelectedItem());
        });

        dropDownPanel.add(videoEncoderLibsBox);
        dropDownPanel.add(videoPresetsBox);
        dropDownPanel.add(crfBox);
        dropDownPanel.add(audioEncoderLibsBox);
        dropDownPanel.add(audioPresetsBox);
        dropDownPanel.add(encoderThreadsBox);
        return dropDownPanel;
    }
}