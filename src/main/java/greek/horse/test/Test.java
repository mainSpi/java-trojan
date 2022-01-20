package greek.horse.test;

import com.github.sarxos.webcam.Webcam;

import javax.swing.*;
import java.awt.*;

public class Test {

    public static void main(String[] args) throws Exception {
        Webcam.getWebcams().stream().map(w -> w.getName()).forEach(System.out::println);
    }

}
