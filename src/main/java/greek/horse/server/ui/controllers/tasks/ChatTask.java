package greek.horse.server.ui.controllers.tasks;

import greek.horse.models.MessageType;
import greek.horse.server.troyStructure.TroyPlebe;
import greek.horse.server.troyStructure.request.RecurrentTroyRequest;
import greek.horse.server.ui.ChatApp;
import greek.horse.server.ui.controllers.ChatController;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatTask implements Runnable {
    private final TroyPlebe plebe;

    private ChatController controller;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public ChatTask(TroyPlebe tp) {
        this.plebe = tp;
    }

    @Override
    public void run() {
        AtomicBoolean start = new AtomicBoolean(false);

        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                stage.setTitle("Chat - " + plebe.getTitle());
                stage.getIcons().add(ChatApp.appIcon);

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("scenes/chat.fxml"));
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

        controller.begin();

        RecurrentTroyRequest request = (RecurrentTroyRequest) this.plebe.startChat();

        request.getObservableReceivedObjs().addListener((ListChangeListener<? super Object>) listener -> {
            listener.next();

            for (Object obj : listener.getAddedSubList()) {
                String line = (String) obj;
                controller.addTextLine(MessageType.CLIENT_NAME, line, MessageType.CLIENT);
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

    public AtomicBoolean getRunning() {
        return running;
    }
}
