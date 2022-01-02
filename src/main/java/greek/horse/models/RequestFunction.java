package greek.horse.models;

import java.io.Serializable;

public enum RequestFunction implements Serializable {


    NET_INFO,

    CHAT_MESSAGE, CHAT_START,

    RELEASE,

    DISCONNECT, TURN_OFF, LOCK, STOP,

    DESKTOP_START, DESKTOP_REFRESH, USER_INPUT, MONITOR_COUNT,

    FILES,

    TERMINAL_START, TERMINAL_COMMAND
}
