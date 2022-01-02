package greek.horse.server.ui;

import javafx.embed.swing.JFXPanel;

import java.io.IOException;
import java.net.URISyntaxException;

public class Launcher {
    public static void main(String[] args) throws URISyntaxException, IOException {
        new JFXPanel();
        ChatApp.main(args);
    }
}
