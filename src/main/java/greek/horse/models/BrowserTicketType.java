package greek.horse.models;

import java.io.Serializable;

public enum BrowserTicketType implements Serializable {
    LOAD_DRIVES, LOAD_FILES, UPLOAD, DELETE, RUN, DOWNLOAD, KNOWN_FOLDERS
}
