import javax.swing.*;
import java.awt.*;

import javax.imageio.*;
import java.io.*;
import javax.sound.sampled.*;

/**
 * A quick side project that plays a "Danger zone" clip when the button is pressed.
 * This was very important to the success of this project.
 */
public class DangerZone {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("DANGER ZONE");
        frame.setSize(240, 256);

        ImageIcon button = new ImageIcon(ImageIO.read(new File("." + File.separator + "redbutton.png")));
        JButton b = new JButton(button);
        b.addActionListener(a -> {
                play();
            });
        frame.add(b);
        frame.setAlwaysOnTop(true);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void play() {
        try {
            File soundFile = new File("." + File.separator + "DANGERZONE.wav");
            AudioInputStream audioStream = AudioSystem
                .getAudioInputStream(soundFile);
            AudioFormat audioFormat = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(
                    SourceDataLine.class, audioFormat);
            SourceDataLine sourceLine = (SourceDataLine) AudioSystem
                .getLine(info);
            sourceLine.open(audioFormat);
            sourceLine.start();
            int numBytesRead = 0;
            int BUFFER_SIZE = 128000;
            byte[] data = new byte[BUFFER_SIZE];
            while (numBytesRead != -1) {
                numBytesRead = audioStream.read(data, 0, data.length);

                if (numBytesRead >= 0) {
                    sourceLine.write(data, 0, numBytesRead);
                }
            }

            sourceLine.drain();
            sourceLine.close();
        } catch (Exception e) {
        }
    }
}