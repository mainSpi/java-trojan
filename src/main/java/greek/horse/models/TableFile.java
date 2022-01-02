package greek.horse.models;

import greek.horse.server.ui.ChatApp;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Locale;

public class TableFile implements Serializable {

    private transient ImageView icon = null;
    private final FileStruct fileStruct;

    public TableFile(FileStruct fileStruct) {
        this.fileStruct = fileStruct;
        BufferedImage bi = ChatApp.getImage(this.fileStruct.getFileType().toString().toLowerCase(Locale.ROOT), 0.09);
        this.icon = new ImageView(SwingFXUtils.toFXImage(bi, null));
    }

//    @Override
//    public int compareTo(@NotNull TableFile o) {
//        return this.fileStruct.getName().compareToIgnoreCase(o.fileStruct.getName());
//    }

    @Override
    public String toString() {
        return "TableFile{" +
                this.fileStruct.toString() +
                '}';
    }

    public SimpleStringProperty nameProperty() {
        return new SimpleStringProperty(this.fileStruct.getName());
    }

    public String getSize() {
        return this.fileStruct.getSize();
    }

    public SimpleStringProperty sizeProperty() {
        return new SimpleStringProperty(this.fileStruct.getSize());
    }

    public SimpleObjectProperty<ImageView> iconProperty() {
        return new SimpleObjectProperty<>(this.icon);
    }

    public Path getPath() {
        return Path.of(this.fileStruct.getPath());
    }

    public FileType getFileType() {
        return this.fileStruct.getFileType();
    }
}
