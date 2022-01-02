package greek.horse.models;

import java.io.Serializable;

public class FileData implements Serializable {
    private byte[] fileBytes;
    private String fileName;

    public FileData(byte[] fileData, String fileName) {
        this.fileBytes = fileData;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    @Override
    public String toString() {
        return "FileData{" +
                "fileName='" + fileName + '\'' +
                '}';
    }
}
