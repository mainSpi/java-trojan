package greek.horse.models;

import java.io.Serializable;
import java.util.ArrayList;

public class FileBrowserResponseWrapper implements Serializable {

    private final boolean error;
    private String description = "";
    private ArrayList<FileStruct> list;
    private FileData fileData;

    public FileBrowserResponseWrapper(ArrayList<FileStruct> list){
        this.list = list;
        this.error = false;
    }

    public FileBrowserResponseWrapper(FileData fileData) {
        this.fileData = fileData;
        this.error = false;
    }

    public FileBrowserResponseWrapper(Exception e) {
        this.error = true;
        this.description = e.getClass().getSimpleName() + ": " + e.getLocalizedMessage();
    }

    public FileBrowserResponseWrapper() {
        this.error = false;
    }

    public boolean isError() {
        return error;
    }

    public ArrayList<FileStruct> getList() {
        return list;
    }

    public String getDescription() {
        return description;
    }

    public FileData getFileData() {
        return fileData;
    }

    @Override
    public String toString() {
        return "FileBrowserResponseWrapper{" +
                "error=" + error +
                ", description='" + description + '\'' +
                ", list=" + list +
                ", fileData=" + fileData +
                '}';
    }
}
