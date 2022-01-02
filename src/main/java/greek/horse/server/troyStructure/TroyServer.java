package greek.horse.server.troyStructure;

import greek.horse.server.ui.controllers.HorseController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class TroyServer implements Runnable {

    private static final Logger log = Logger.getLogger(TroyServer.class);

    private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final ObservableList<TroyPlebe> plebes = FXCollections.synchronizedObservableList(FXCollections.observableList(new CopyOnWriteArrayList<>()));
    private final int port;
    private final HorseController controller;

    private ServerSocket server;

    private final AtomicBoolean active = new AtomicBoolean(true);

    public TroyServer(int port, HorseController controller) {
        this.port = port;
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(this.port);

            while (this.active.get()) {

                if (!this.active.get() && plebes.size() == 0) { // its off and has no plebes
                    break;
                }

                Socket accept = server.accept();
                threadPool.execute(new ServerSocketManager(this, accept));

            }
            stop();
        }catch (BindException e){
            Platform.runLater(() -> {
                this.controller.startBtn.setSelected(false);
                this.controller.showDialog("Error binding port", "Port already in use, close other programs using port "+port, Alert.AlertType.ERROR);
            });
        } catch (Exception e) {
            log.error("Error on socket creation", e);
        }

    }

    public void removePlebe(String id) {
        if (id.isEmpty()) {
            return;
        }
        try {
            ArrayList<TroyPlebe> removeList = new ArrayList<>();
            plebes.parallelStream()
                    .filter(tp -> tp.getId().contentEquals(id))
                    .forEach(tp -> {
                        tp.setRunning(false);
                        removeList.add(tp);
                    });
            removeList.forEach(ple -> plebes.remove(ple));
        } catch (ConcurrentModificationException e) {
            log.error("Concurrent error. TroyPlebe may not have been removed.");
        }
    }

    public void stop() {
        this.active.set(false);
        try {
            plebes.forEach(p -> p.setRunning(false));
            plebes.clear();
            if (server != null) {
                server.close();
            }
        } catch (IOException e) {
            log.error("Exception closing server.");
        }

    }

    public ObservableList<TroyPlebe> getPlebes() {
        return plebes;
    }

    public AtomicBoolean isActive() {
        return active;
    }

    public void addPlebe(TroyPlebe troyPlebeNew) {
        this.plebes.add(troyPlebeNew);
    }

    public void executeInPool(Runnable run) {
        threadPool.execute(run);
    }
}
