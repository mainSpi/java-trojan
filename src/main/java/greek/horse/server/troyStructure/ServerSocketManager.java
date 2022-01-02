package greek.horse.server.troyStructure;

import greek.horse.models.FunctionTicket;
import greek.horse.server.troyStructure.request.TroyRequest;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ServerSocketManager implements Runnable {

    private String plebeID = "";
    private final AtomicBoolean active = new AtomicBoolean(true);
    private final TroyServer father;
    private final Socket socket;
    private static final Logger log = Logger.getLogger(ServerSocketManager.class);

    public ServerSocketManager(TroyServer father, Socket socket) {
        this.father = father;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed() && father.isActive().get() && this.active.get()) {

                Runnable readTask = manageRead();
                Runnable writeTask = manageWrite();

                this.father.executeInPool(readTask);
                this.father.executeInPool(writeTask);

                while (this.active.get()) {
                    Thread.sleep(100);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                father.removePlebe(plebeID);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private Runnable manageWrite() {
        return () -> {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                while (this.active.get()) {

                    if (this.plebeID.isEmpty()) {
                        Thread.sleep(100);
                        continue;
                    }

                    TroyPlebe tp = this.father.getPlebes().parallelStream()
                            .filter(plebe -> plebe.getId().contentEquals(this.plebeID))
                            .findFirst().get();

                    HashMap<FunctionTicket, Object> writeMap = compileRequests(tp.getRequests());

                    if (writeMap.keySet().size() > 0){
                        log.debug("writeMap: "+writeMap);
                        oos.writeObject(writeMap);
                    }

                    Thread.sleep(300);
                }
            } catch (Exception e) {
                this.active.set(false);
                log.error("Error while writing to socket.",e);
            }
        };
    }

    private HashMap<FunctionTicket,Object> compileRequests(List<TroyRequest> requests) {
        return (HashMap<FunctionTicket,Object>) requests.parallelStream().filter(req -> !req.getSent().get())
                .filter(req -> {
                    req.getSent().set(true);
                    return true;
                })
                .collect(Collectors
                        .toMap(TroyRequest::getTicket, TroyRequest::getSendObj));
    }

    private Runnable manageRead() {
        return () -> {
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                while (this.active.get()) {

                    Object receivedId = ois.readObject();

                    if (!doVirginCheck(receivedId)) { // if its not virgin
                        HashMap<FunctionTicket, Object> receivedMap = (HashMap<FunctionTicket, Object>) receivedId;

                        TroyPlebe tp = this.father.getPlebes().parallelStream()
                                .filter(plebe -> plebe.getId().contentEquals(this.plebeID))
                                .findFirst().get();

                        tp.getRequests().forEach(req -> {
                            FunctionTicket ticket = req.getTicket();
                            if (receivedMap.containsKey(ticket)) {
                                req.addReceivedObj(receivedMap.get(ticket));
                            }
                        });

                    }

                }
            } catch (Exception e) {
                this.active.set(false);
                log.error("Error while reading from socket.",e);
            }
        };
    }

    private boolean doVirginCheck(Object o) {
        if (this.plebeID.isEmpty()) {
            this.plebeID = (String) o;
            this.father.addPlebe(new TroyPlebe(this.plebeID));
            log.info("Registered new client. Id: " + this.plebeID);
            return true;
        }
        return false;
    }
}
