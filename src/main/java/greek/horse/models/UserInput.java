package greek.horse.models;

import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserInput implements Serializable {

    private int code;
    private int key;
    private Mode mode;

    private int x;
    private int y;

    private final long id = System.currentTimeMillis();
    private final AtomicBoolean paired = new AtomicBoolean(false);

    public UserInput(int key, Mode mode) {
        this.key = key;
        this.mode = mode;
    }


    public UserInput(int button, int x, int y, Mode mode) {
        this.code = button;
        this.x = x;
        this.y = y;
        this.mode = mode;
    }

    public int getCode() {
        return code;
    }

    public int getKey() {
        return key;
    }

    public Mode getMode() {
        return mode;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "UserInput{" +
                "code=" + code +
                ", key='" + key + " - " + KeyEvent.getKeyText(key) +
                ", mode=" + mode +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInput userInput = (UserInput) o;
        return id == userInput.id && mode == userInput.mode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode, id);
    }

    public void setPaired(boolean b) {
        this.paired.set(b);
    }

    public boolean getPaired() {
        return this.paired.get();
    }
}


