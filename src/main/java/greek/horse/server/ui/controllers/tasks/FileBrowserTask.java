package greek.horse.server.ui.controllers.tasks;

import greek.horse.server.troyStructure.TroyPlebe;
import greek.horse.server.ui.ChatApp;
import greek.horse.server.ui.controllers.FileBrowserController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileBrowserTask implements Runnable {
    private final TroyPlebe plebe;

    private FileBrowserController controller;

    public FileBrowserTask(TroyPlebe tp) {
        this.plebe = tp;
    }

    @Override
    public void run() {
        AtomicBoolean start = new AtomicBoolean(false);

        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                stage.setTitle("File Browser - " + plebe.getTitle());
                stage.getIcons().add(ChatApp.appIcon);

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("scenes/filesBrowser.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root);
                stage.setScene(scene);

                controller = loader.getController();
                controller.setPlebe(plebe);
                controller.setStage(stage);
                controller.setTitle(plebe.getTitle());

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

        controller.begin();

        while (plebe.getRunning().get()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Platform.runLater(() -> {
            controller.getStage().hide();
        });

    }

}
