package greek.horse.models;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class FileBrowserTicket implements Serializable {
    private ArrayList<String> uri;
    private BrowserTicketType ticketType;

    private FileData fileData;

    public FileBrowserTicket(BrowserTicketType ticketType) {
        this.ticketType = ticketType;
    }

    public FileBrowserTicket(String uri, BrowserTicketType ticketType) {
        this.uri = splitUri(uri);
        this.ticketType = ticketType;
    }

    public FileBrowserTicket(String uri, BrowserTicketType ticketType, String fileName, byte[] fileData) {
        this.uri = splitUri(uri);
        this.ticketType = ticketType;
        this.fileData = new FileData(fileData, fileName);
    }

    private ArrayList<String> splitUri(String uri) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < uri.length(); i++) {
            char c = uri.charAt(i);
            if (c == File.separatorChar) {
                sb.append(";");
            } else {
                sb.append(c);
            }
        }

        String cmp = sb.toString();
        return new ArrayList<>(Arrays.asList(cmp.split(";")));
    }

    public String getUri() {
        StringBuilder sb = new StringBuilder();

        if (!this.uri.isEmpty()) {
            this.uri.forEach(s -> sb.append(s).append(File.separator));
            sb.substring(0, sb.length() - 2);
        } else {
            sb.append(File.separator);
        }
        return sb.toString();
    }

    public BrowserTicketType getTicketType() {
        return ticketType;
    }

    public FileData getFileData() {
        return fileData;
    }

    @Override
    public String toString() {
        return "FileBrowserTicket{" +
                "uri=" + uri +
                ", ticket=" + ticketType +
                ", fileData=" + fileData +
                '}';
    }
}
