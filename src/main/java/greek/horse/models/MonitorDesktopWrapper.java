package greek.horse.models;

import java.io.Serializable;

public class MonitorDesktopWrapper implements Serializable {
    private final double width;
    private final double height;
    private final boolean compressed;
    private int monitor;

    public MonitorDesktopWrapper(double w, double Height, boolean compressed, int monitor) {

        this.width = w;
        this.height = Height;
        this.compressed = compressed;
        this.monitor = monitor;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public int getMonitor() {
        return monitor;
    }

    @Override
    public String toString() {
        return "MonitorDesktopWrapper{" +
                "width=" + width +
                ", height=" + height +
                ", compressed=" + compressed +
                ", monitor=" + monitor +
                '}';
    }
}
