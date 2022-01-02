package greek.horse.server.troyStructure.request;

import greek.horse.models.FunctionTicket;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface TroyRequest {
    Object getSendObj();

    void addReceivedObj(Object receivedObj);

    AtomicBoolean getSent();

    FunctionTicket getTicket();

    List<Object> getReceivedObjs() throws Exception;
}
