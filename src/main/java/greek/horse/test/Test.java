package greek.horse.test;

import javax.swing.*;
import java.awt.*;

public class Test {

    public static void main(String[] args) throws Exception {
        int w = 0, h = 0, x = 0, y = 0;
        for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            Rectangle bounds = device.getDefaultConfiguration().getBounds();
            System.out.println(bounds);
            w += bounds.getWidth();
            w += Math.abs(bounds.getX());
            h += bounds.getHeight();
            h += Math.abs(bounds.getY());
            x = x > bounds.getX() ? x : (int) bounds.getX();
            y = y < bounds.getY() ? y : (int) bounds.getY();
        }
        System.out.println();
        System.out.println(new Rectangle(0, 0, 10000, 10000));

        JFrame frame = new JFrame();
        frame.setSize(new Dimension((int) w, (int) h));
        frame.setLocation(0, 0);
        frame.setUndecorated(false);
        frame.setVisible(true);

    }

}
