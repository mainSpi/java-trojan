package greek.horse.server.ui.controllers;

import greek.horse.server.ui.controllers.tasks.WebcamTask;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.awt.image.BufferedImage;

public class WebcamController {
    public ImageView imageView;
    public ChoiceBox<String> choiceBox;
    public Text statusCircleText;
    public Text fpsText;

    private WebcamTask task;

    public void configureChoiceBox() {
        choiceBox.getSelectionModel().selectFirst();
//        choiceBox.getSelectionModel().selectedIndexProperty()
//                .addListener((observableValue, oldIndex, newIndex) -> {
//                    if (oldIndex.intValue() != newIndex.intValue()) {
//                        task.refreshSettings(newIndex.intValue());
//                    }
//                });
    }

    public void setImage(BufferedImage screenCapture) {
        imageView.setFitWidth(screenCapture.getWidth());
        imageView.setFitHeight(screenCapture.getHeight());
        imageView.setImage(SwingFXUtils.toFXImage(screenCapture, null));
    }

    public void setFpsText(double fps) {
        if (fps >= 4) {
            statusCircleText.setFill(Color.rgb(0, 150, 0));
        } else if (fps == 3) {
            statusCircleText.setFill(Color.DARKGOLDENROD);
        } else {
            statusCircleText.setFill(Color.rgb(200, 0, 0));

        }
        this.fpsText.setText(String.format("%.2f", fps).replace(",", "."));
    }

    public void setTask(WebcamTask webcamTask) {
        this.task = webcamTask;
    }
}
