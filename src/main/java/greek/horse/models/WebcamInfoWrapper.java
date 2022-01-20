package greek.horse.models;

import java.io.Serializable;

public class WebcamInfoWrapper implements Serializable {
    private final double width;
    private final double height;
    private final String webcamName;

    public WebcamInfoWrapper(double width, double h, String selectedIndex) {
        this.width = width;
        this.height = h;
        this.webcamName = selectedIndex;
    }

    @Override
    public String toString() {
        return "WebcamInfoWrapper{" +
                "width=" + width +
                ", height=" + height +
                ", webcamName=" + webcamName +
                '}';
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public String getWebcamName() {
        return webcamName;
    }
}
