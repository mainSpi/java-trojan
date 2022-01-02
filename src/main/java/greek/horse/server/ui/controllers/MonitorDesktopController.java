package greek.horse.server.ui.controllers;

import greek.horse.models.Mode;
import greek.horse.models.UserInput;
import greek.horse.server.ui.controllers.tasks.MonitorDesktopTask;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MonitorDesktopController {
    public ImageView imageView;
    public CheckBox compressionCheck;
    public CheckBox keysCheck;
    public CheckBox clicksCheck;
    public Text fpsText;
    public ChoiceBox<String> choiceBox;
    public Text statusCircleText;
    private Stage stage;

    private final CopyOnWriteArrayList<UserInput> clicksInputs = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<UserInput> keysInputs = new CopyOnWriteArrayList<>();
    private final static HashMap<Integer, Integer> things = createMap();
    private MonitorDesktopTask task;

    private static HashMap<Integer, Integer> createMap() {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(129, KeyEvent.VK_DEAD_ACUTE);
        map.put(131, KeyEvent.VK_DEAD_TILDE);
        map.put(222, KeyEvent.VK_QUOTE);
        return map;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.getScene().setOnKeyPressed(e -> {
            if (stage.isShowing() && isSendingKeys()) {
                UserInput press = new UserInput(e.getCode().getCode(), Mode.KEY_PRESSED);
                keysInputs.add(press);
                if (things.containsKey(e.getCode().getCode())) {
                    keysInputs.add(new UserInput(e.getCode().getCode(), Mode.KEY_RELEASED));
                    press.setPaired(true);
                }
            }
            e.consume();
        });
        stage.getScene().setOnKeyReleased(e -> {
            if (stage.isShowing() && isSendingKeys()) {
                UserInput pair = keysInputs.parallelStream().filter(ui -> !ui.getPaired() && ui.getMode().equals(Mode.KEY_PRESSED) && ui.getKey() == e.getCode().getCode()).findFirst().get();
                pair.setPaired(true);
                keysInputs.add(new UserInput(e.getCode().getCode(), Mode.KEY_RELEASED));
            }
            e.consume();
        });
    }

    public void configureChoiceBox() {
        choiceBox.getSelectionModel().selectFirst();
        choiceBox.getSelectionModel().selectedIndexProperty()
                .addListener((observableValue, oldIndex, newIndex) -> {
                    task.refreshSettings();
                });
    }

    public Stage getStage() {
        return stage;
    }

    public void setImage(BufferedImage screenCapture) {
        imageView.setFitWidth(screenCapture.getWidth());
        imageView.setFitHeight(screenCapture.getHeight());
        imageView.setImage(SwingFXUtils.toFXImage(screenCapture, null));
    }

    public boolean isCompressed() {
        return compressionCheck.isSelected();
    }

    public boolean isSendingKeys() {
        return keysCheck.isSelected();
    }

    public boolean isSendingClicks() {
        return clicksCheck.isSelected();
    }

    private int getButton(MouseButton button) {
        return switch (button) {
            case PRIMARY -> InputEvent.BUTTON1_DOWN_MASK;
            case SECONDARY -> InputEvent.BUTTON3_DOWN_MASK;
            default -> 0;
        };
    }

    public List<UserInput> getInputs() {
        ArrayList<UserInput> sincList = new ArrayList<>(keysInputs);
        boolean allClosed = sincList.parallelStream().allMatch(ui -> ui.getMode().equals(Mode.KEY_RELEASED)
                || ui.getPaired());
        ArrayList<UserInput> ret;
        if (allClosed) {
            ret = new ArrayList<>();
            ret.addAll(sincList);
            ret.addAll(clicksInputs);

            keysInputs.removeAll(sincList);
        } else {
            ret = new ArrayList<>(clicksInputs);
        }
        clicksInputs.clear();
        return ret;
    }

    public void mousePressed(MouseEvent event) {
        if (isSendingClicks() &&
                (event.getButton().equals(MouseButton.PRIMARY) || event.getButton().equals(MouseButton.SECONDARY))) {
            this.clicksInputs.add(new UserInput(getButton(event.getButton()), (int) event.getX(), (int) event.getY(), Mode.CLICK_PRESSED));
        }
    }

    public void MouseReleased(MouseEvent event) {
        if (isSendingClicks() &&
                (event.getButton().equals(MouseButton.PRIMARY) || event.getButton().equals(MouseButton.SECONDARY))) {
            this.clicksInputs.add(new UserInput(getButton(event.getButton()), (int) event.getX(), (int) event.getY(), Mode.CLICK_RELEASED));
        }
    }

    public void setTask(MonitorDesktopTask monitorDesktopTask) {
        this.task = monitorDesktopTask;
    }

    public void toggleImageCompression(ActionEvent event) {
        this.task.refreshSettings();
    }

    public void setFpsText(int fps) {
        if (fps>=4){
            statusCircleText.setFill(Color.rgb(0,150,0));
        } else if (fps==3){
            statusCircleText.setFill(Color.DARKGOLDENROD);
        } else {
            statusCircleText.setFill(Color.rgb(200,0,0));

        }
        this.fpsText.setText(String.valueOf(fps));
    }
}
