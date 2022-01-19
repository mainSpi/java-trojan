package greek.horse.server.troyStructure;

import greek.horse.models.NetInfo;
import greek.horse.server.ui.ChatApp;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.util.Locale;

public class NetInfoTable {

    private final ImageView imageCountry;
    private final SimpleStringProperty userName;
    private final SimpleStringProperty ip;
    private final SimpleStringProperty country;
    private final SimpleStringProperty region;
    private final SimpleStringProperty city;
    private final SimpleStringProperty isp;
    private final SimpleStringProperty os;
    private final SimpleStringProperty bigOs;
    private final SimpleStringProperty headlessText;
    private final boolean headless;
    private final TroyPlebe father;

    public NetInfoTable(NetInfo netInfo, TroyPlebe father) {
        this.userName = new SimpleStringProperty(netInfo.getUserName());
        this.ip = new SimpleStringProperty(netInfo.getExternalIP());
        this.country = new SimpleStringProperty(netInfo.getCountry());
        this.region = new SimpleStringProperty(netInfo.getRegion());
        this.city = new SimpleStringProperty(netInfo.getCity());
        this.isp = new SimpleStringProperty(netInfo.getIsp());
        this.os = new SimpleStringProperty(netInfo.getOs().toString());
        this.bigOs = new SimpleStringProperty(netInfo.getBigOs());
        this.headlessText = new SimpleStringProperty(netInfo.isHeadless()?"Yes":"No");
        this.headless = netInfo.isHeadless();
        this.father = father;

        String countryName = netInfo.getCountry().toLowerCase(Locale.ROOT);

        BufferedImage bi = ChatApp.getImage(countryName, 0.08, 0, 85, 512, 340);
        this.imageCountry = new ImageView(SwingFXUtils.toFXImage(bi, null));
    }

    public ImageView getImageCountry() {
        return imageCountry;
    }

    public String getUserName() {
        return userName.get();
    }

    public SimpleStringProperty userNameProperty() {
        return userName;
    }

    public String getIp() {
        return ip.get();
    }

    public SimpleStringProperty ipProperty() {
        return ip;
    }

    public String getCountry() {
        return country.get();
    }

    public SimpleStringProperty countryProperty() {
        return country;
    }

    public String getRegion() {
        return region.get();
    }

    public SimpleStringProperty regionProperty() {
        return region;
    }

    public String getCity() {
        return city.get();
    }

    public SimpleStringProperty cityProperty() {
        return city;
    }

    public TroyPlebe getFather() {
        return father;
    }

    public String getIsp() {
        return isp.get();
    }

    public SimpleStringProperty ispProperty() {
        return isp;
    }

    public String getOs() {
        return os.get();
    }

    public SimpleStringProperty osProperty() {
        return os;
    }

    public String getBigOs() {
        return bigOs.get();
    }

    public boolean isHeadless() {
        return headless;
    }

    public String getHeadlessText() {
        return headlessText.get();
    }

    public SimpleStringProperty headlessTextProperty() {
        return headlessText;
    }

    public SimpleStringProperty bigOsProperty() {
        return bigOs;
    }

    public SimpleObjectProperty<ImageView> imageProperty(){
        return new SimpleObjectProperty<>(this.imageCountry);
    }

    @Override
    public String toString() {
        return "NetInfoTable{" +
                "userName=" + userName +
                ", ip=" + ip +
                ", country=" + country +
                ", region=" + region +
                ", city=" + city +
                ", isp=" + isp +
                ", os=" + os +
                '}';
    }

}
