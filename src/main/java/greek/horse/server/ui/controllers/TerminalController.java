package greek.horse.server.ui.controllers;

import greek.horse.server.troyStructure.TroyPlebe;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class TerminalController {
    public TextField textFieldCommand;
    public TextFlow textFlow;
    public ScrollPane scrollPane;
    private TroyPlebe plebe;
    private Stage stage;


    public void begin() {
        scrollPane.vvalueProperty().bind(textFlow.heightProperty());
        textFieldCommand.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                sendCommand();
            }
        });
    }

    public void setPlebe(TroyPlebe plebe) {
        this.plebe = plebe;
    }

    public void sendBtn(ActionEvent event) {
        sendCommand();
    }

    private void sendCommand() {
        String command = textFieldCommand.getText();
        if (command.isEmpty()) {
            return;
        }
        this.plebe.sendTerminalCommand(command);
        textFieldCommand.setText("");
    }

    public void addTextLine(String line) {
        Text newLine = new Text(line.replaceAll("\n", "").replaceAll("\r", "").concat("\n"));
        newLine.setFill(Color.WHITE);
        newLine.setFont(Font.font("Consolas", FontWeight.NORMAL, FontPosture.REGULAR, 16));

        Platform.runLater(() -> {
            textFlow.getChildren().add(newLine);
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }
}
