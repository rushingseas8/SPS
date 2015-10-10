import javafx.geometry.Point3D;
import javax.swing.*;
import java.awt.*;

public class Grapher {
    private static JFrame frame;
    private static Drawer panel;

    private static Point3D a;
    private static Object b;

    //-30 to 30 x and y

    public static void main() {
        frame = new JFrame("Grapher!");
        frame.setSize(640, 480);

        panel = new Drawer();
        panel.setSize(640, 480);
        frame.add(panel);

        new Thread() {
            public void run() {
                while(true) {
                    panel.repaint();
                    try {
                        Thread.sleep(60);
                    } catch (Exception e) {}
                }
            }
        }.start();

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public static void graph(Point3D aa, Object bb) {
        a = aa;
        b = bb;
    }

    private static class Drawer extends JPanel {
        public void graph(Point3D aa, Object bb) {
            a = aa;
            b = bb;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.BLACK);
            g.fillRect((getWidth() / 2) - 1, 0, 2, getHeight());
            g.fillRect(0, (getHeight() / 2) - 1, getWidth(), 2);

            if(a != null) {
                //System.out.println(a.getX() + ", " + a.getY());
                g.setColor(b == null ? Color.GREEN : Color.RED);
                g.fillOval((int)(getWidth() * a.getX() / 100.0) + (getWidth() / 2) - 5,
                    (int)(-getHeight() * a.getY() / 100.0) + (getHeight() / 2) - 5, 10, 10);
            } else {
                //System.out.println("a is null");
            }
        }
    }
}