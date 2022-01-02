package greek.horse.server.troyStructure.request;

import greek.horse.models.RequestFunction;
import greek.horse.models.FunctionTicket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecurrentTroyRequest implements TroyRequest {
    private final Object sendObj;
    private final FunctionTicket ticket;
    private final AtomicBoolean released = new AtomicBoolean(false);

    private final ObservableList<Object> receivedObjs = FXCollections.observableArrayList();

    private final AtomicBoolean sent = new AtomicBoolean(false);

    public RecurrentTroyRequest(Object sendObj, RequestFunction f) {
        this.sendObj = sendObj;
        this.ticket = new FunctionTicket(f, String.valueOf(System.currentTimeMillis()) + sendObj.hashCode(), true);
    }

    @Override
    public Object getSendObj() {
        return sendObj;
    }

    @Override
    public synchronized void addReceivedObj(Object receivedObj) {
        receivedObjs.add(receivedObj);
    }

    @Override
    public AtomicBoolean getSent() {
        return sent;
    }

    @Override
    public FunctionTicket getTicket() {
        return ticket;
    }

    @Override
    public List<Object> getReceivedObjs(){
        return getObservableReceivedObjs();
    }

    public ObservableList<Object> getObservableReceivedObjs(){
        return receivedObjs;
    }

    public AtomicBoolean getReleased() {
        return released;
    }
}