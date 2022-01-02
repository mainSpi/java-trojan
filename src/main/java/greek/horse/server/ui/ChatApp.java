package greek.horse.server.ui;

import greek.horse.test.Test;
import greek.horse.server.ui.controllers.HorseController;
import greek.horse.server.ui.controllers.MonitorDesktopController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ChatApp extends Application {

    private HorseController controller;
    public static Image appIcon = new Image(ChatApp.class.getClassLoader().getResource("images/icon.png").toString());
    public static final ConcurrentHashMap<String, Image> imgs = new ConcurrentHashMap<>();
    private static final Logger log = Logger.getLogger(MonitorDesktopController.class);

    public static void main(String[] args) throws URISyntaxException, IOException {
        loadImages();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            primaryStage.setTitle("Java Horse");
            primaryStage.getIcons().add(appIcon);
            primaryStage.setResizable(false);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("scenes/horse.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);

            primaryStage.show();
            controller = loader.getController();
            controller.setStage(primaryStage);

            primaryStage.setOnCloseRequest(e -> {
                controller.stopServer();
                System.exit(1);
            });

        } catch (Exception ex) {
            exceptionAlert(ex, primaryStage);
        }
        new Thread(() -> {
            while (primaryStage.isShowing()) {
                System.gc();
                try {
                    Thread.sleep(15 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void exceptionAlert(Exception ex, Stage stage) {
        log.error("exception alert: " + ex);
//        ex.printStackTrace();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception Stack");
            alert.setHeaderText("An Exception was thrown");
            alert.setContentText("This is a very unusual and severe error. Please send the stack trace in the text box below to the developer, this helps to improve the tool.");
            alert.initModality(Modality.APPLICATION_MODAL);

            if (stage != null) {
                alert.initOwner(stage);
            }

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(expContent);
            ex.printStackTrace();
            alert.showAndWait();
            System.exit(1);
        });
    }

    private static void loadImages() {
        try {
            new JFXPanel(); // just to load internal components, i was getting some weird errors

            List<String> list = new ArrayList<>();

            // Jar mode
            CodeSource src = ChatApp.class.getProtectionDomain().getCodeSource();
            if (src != null) {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ZipEntry ze;
                while ((ze = zip.getNextEntry()) != null) {
                    String entryName = ze.getName();
                    if ((entryName.startsWith("images/flags") || entryName.startsWith("images/diverse"))
                            && entryName.endsWith(".png")) {
                        list.add(entryName);
                    }
                }

            }
            list.forEach(s -> {
                imgs.put(s.split("/")[2], new Image(s));
            });

            //Debugger mode
            if (list.isEmpty()) {
                for (URL resource :
                        Arrays.asList(Test.class.getClassLoader().getResource("images/flags"), Test.class.getClassLoader().getResource("images/diverse"))) {

                    File path = new File(resource.toURI());
                    File[] files = path.listFiles();
                    List<File> l = Arrays.asList(files);
                    for (File f : l) {
                        list.add(f.getPath());
                    }
                    list.forEach(s -> {
                        imgs.put(s.split("\\\\")[s.split("\\\\").length - 1], new Image(new File(s).toURI().toString()));
                    });

                }

            }

        } catch (Exception e) {
            log.error("Error loading images: ", e);
        }
    }

    public static BufferedImage getImage(String imageName, double scale) {
        try {
            Optional<String> first = ChatApp.imgs.keySet().stream()
                    .filter(s -> s.toLowerCase(Locale.ROOT)
                            .contains(imageName
                                    .toLowerCase(Locale.ROOT)
                                    .replaceAll(" ", "-")) && s.contains(".")
                            ).findFirst();
            if (first.isPresent()) {
                String key = first.get();
                BufferedImage bufferedImage = SwingFXUtils
                        .fromFXImage(ChatApp.imgs.get(key), null);
                return Thumbnails.of(bufferedImage)
                        .scale(scale)
                        .asBufferedImage();
            } else {
                String key = ChatApp.imgs.keySet().stream().filter(s -> s.toLowerCase(Locale.ROOT).contains("notfound")).findFirst().get();
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(ChatApp.imgs.get(key), null);
                return Thumbnails.of(bufferedImage).scale(scale).asBufferedImage();
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error treating image: ", e);
        }
        return null;
    }

    public static BufferedImage getImage(String imageName, double scale, int x, int y, int w, int h) {
        try {
            Optional<String> first = ChatApp.imgs.keySet().stream()
                    .filter(s -> s.toLowerCase(Locale.ROOT)
                            .contains(imageName
                                    .toLowerCase(Locale.ROOT)
                                    .replaceAll(" ", "-")) && s.contains(".")
                            ).findFirst();
            if (first.isPresent()) {
                String key = first.get();
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(ChatApp.imgs.get(key), null).getSubimage(x, y, w, h);
                return Thumbnails.of(bufferedImage).scale(scale).asBufferedImage();
            } else {
                String key = ChatApp.imgs.keySet().stream().filter(s -> s.toLowerCase(Locale.ROOT).contains("notfound")).findFirst().get();
                BufferedImage bufferedImage = Thumbnails.of(SwingFXUtils.fromFXImage(ChatApp.imgs.get(key), null)).forceSize(w, h).asBufferedImage();
                return Thumbnails.of(bufferedImage).scale(scale).asBufferedImage();
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error treating image: ", e);
        }
        return null;
    }
}
