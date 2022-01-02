package greek.horse.server.ui.formmaters;

import greek.horse.models.TableFile;
import greek.horse.server.ui.ChatApp;
import greek.horse.server.ui.controllers.FileBrowserController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

public class FileRowsFactory implements Callback<TableView<TableFile>, TableRow<TableFile>> {
    private final FileBrowserController controller;
    private final boolean drives;

    public FileRowsFactory(FileBrowserController controller, boolean drives) {
        this.controller = controller;
        this.drives = drives;
    }

    @Override
    public TableRow<TableFile> call(TableView<TableFile> drivesTable) {
        TableRow<TableFile> row = new TableRow<>();

        row.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() > 1) {
                this.controller.loadFolderFromRow(row);
            }
        });

        if (!drives) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem refresh = new MenuItem("Refresh", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("refresh", 0.25), null)));
            MenuItem upload = new MenuItem("Upload", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("upload", 0.25), null)));
            MenuItem run = new MenuItem("Run", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("run", 0.25), null)));
            MenuItem delete = new MenuItem("Delete", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("delete", 0.25), null)));
            MenuItem download = new MenuItem("Download", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("download", 0.25), null)));
            MenuItem open = new MenuItem("Open", new ImageView(SwingFXUtils.toFXImage(ChatApp.getImage("open", 0.25), null)));

            refresh.setOnAction((event) -> {
                this.controller.loadFromTextField();
            });

            run.setOnAction((event -> {
                this.controller.runFromRow(row);
            }));

            upload.setOnAction(event -> {
                this.controller.uploadFromRow(row);
            });

            delete.setOnAction(event -> {
                this.controller.deleteFromRow(row);
            });

            download.setOnAction(event -> {
                this.controller.downloadFromRow(row);
            });

            open.setOnAction(event -> {
                this.controller.openFromRow(row);
            });
//            menuItem2.setOnAction((event) -> {
//                this.controller.contextDisconnect(row);
//            });
//
//            run.setOnAction((event) -> {
//                this.controller.contextFilesBrowser(row);
//            });

            contextMenu.getItems().addAll(refresh, run, open, upload, download, delete);

            row.setContextMenu(contextMenu);
        }

        return row;
    }
}
