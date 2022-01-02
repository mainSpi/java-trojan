package greek.horse.server.ui.controllers;

import greek.horse.models.MessageType;
import greek.horse.server.troyStructure.TroyPlebe;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class ChatController {
    public ScrollPane scrollPane;
    public TextFlow textFlow;
    public TextField textFieldMessage;
    private TroyPlebe plebe;
    private Stage stage;

    public void begin() {
        scrollPane.vvalueProperty().bind(textFlow.heightProperty());
        textFieldMessage.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                sendMessage();
            }
        });
        sendMessage("Connected");
    }

    private void sendMessage() {
        sendMessage(textFieldMessage.getText());
    }

    private void sendMessage(String message) {
        if (message.isEmpty()) {
            return;
        }
        this.plebe.sendChatMessage(message);
        addTextLine(MessageType.SELF, message, MessageType.SERVER);
        textFieldMessage.setText("");
    }

    public void addTextLine(String name, String text, MessageType type) {
        Text nameText = new Text(name);
        Text messageText = new Text(" > " + text.replaceAll("\n", "").replaceAll("\r", "").concat("\n"));

        switch (type) {
            case CLIENT -> nameText.setFill(Color.BLUE);
            case SERVER -> nameText.setFill(Color.rgb(200, 0, 0));
        }

        nameText.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 16));
        messageText.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 16));

        Platform.runLater(() -> {
            textFlow.getChildren().addAll(nameText, messageText);
        });
    }

    public void sendBtn(ActionEvent event) {
        sendMessage();
    }

    public Stage getStage() {
        return stage;
    }

    public void setPlebe(TroyPlebe plebe) {
        this.plebe = plebe;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
