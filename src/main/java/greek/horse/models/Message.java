package greek.horse.models;

import java.io.Serializable;

public class Message implements Serializable {
    // RSA
    // Blowfish
    // leet (safest)
    private String text;

    public Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
