package greek.horse.server.troyStructure.request;

import greek.horse.models.RequestFunctionType;
import greek.horse.models.FunctionTicket;
import greek.horse.server.troyStructure.TroyPlebe;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class UniqueTroyRequest implements TroyRequest {
    private final TroyPlebe father;
    private final Object sendObj;
    private final FunctionTicket ticket;
    private volatile Object receivedObj;
    private final AtomicBoolean sent = new AtomicBoolean(false);
    private final boolean haveResponse;
    private static final Logger log = Logger.getLogger(UniqueTroyRequest.class);

    public UniqueTroyRequest(TroyPlebe father, Object sendObj, RequestFunctionType f, boolean haveResponse) {
        this.father = father;
        this.sendObj = sendObj;
        this.ticket = new FunctionTicket(f, String.valueOf(System.currentTimeMillis()) + sendObj.hashCode(), false);
        this.haveResponse = haveResponse;
    }

    @Override
    public Object getSendObj() {
        return sendObj;
    }

    @Override
    public synchronized void addReceivedObj(Object receivedObj) {
        this.receivedObj = receivedObj;
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
    public List<Object> getReceivedObjs() throws Exception{
        return Collections.singletonList(getReceivedObj());
    }

    public Object getReceivedObj() throws Exception{
        try {
            while (haveResponse ? (this.receivedObj == null) : (this.sent.get())) {
                if (!father.haveRequest(this) || !father.getRunning().get()) {
                    father.removeRequest(this);
                    break;
                }
                Thread.sleep(50);
            }
            father.removeRequest(this);
            return receivedObj;
        } catch (Exception e){
            log.error(e);
            return null;
        }
    }
}
