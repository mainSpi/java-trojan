package greek.horse.client.tasks;

import com.formdev.flatlaf.FlatLightLaf;
import greek.horse.client.ChatUI;
import greek.horse.client.ClientSocketManager;
import greek.horse.models.FunctionTicket;
import greek.horse.models.MessageType;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

public class ClientChatTask implements Runnable {
    private final ClientSocketManager client;
    private FunctionTicket ticket;
    private final ChatUI frame;

    private static final Logger log = Logger.getLogger(ClientChatTask.class);

    public ClientChatTask(ClientSocketManager clientSocketManager) {
        client = clientSocketManager;
        FlatLightLaf.setup();
        frame = new ChatUI();
    }

    @Override
    public void run() {
        try {
            while (this.client.getRunning().get()) {
                if (ticket != null && this.client.getFixedMap().containsKey(ticket)) {
                    frame.setSendListener(e -> {
                        if (e.getActionCommand().isEmpty()){
                            return;
                        }
                        try {
                            sendMessage(e.getActionCommand());
                        } catch (Exception ex) {
                            log.error("Error sending message", ex);
                        }

                    });
                    frame.setVisible(true);
                    sendMessage("Connected");

                    while (this.client.getRunning().get() && this.client.getFixedMap().containsKey(ticket)){
                        Thread.sleep(200);
                    }

                    closeChat();

//                } else {
//
                }
                Thread.sleep(500);
            }
//            closeChat();
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void sendMessage(String message) throws IOException {
        HashMap<FunctionTicket, Object> answerMap = new HashMap<>();
        answerMap.put(ticket, message);
        client.getOos().getAcquire().writeObject(answerMap);
        client.getOos().getAcquire().flush();
        frame.addLine(MessageType.SELF, message, MessageType.CLIENT);
    }

    private void closeChat() {
        frame.setVisible(false);
        frame.removeSendListener();
        frame.cleanMessages();
    }

    public void setTicket(FunctionTicket ticket) {
        this.ticket = ticket;
    }

    public void addMessage(String o) {
        frame.addLine(MessageType.SERVER_NAME, o, MessageType.SERVER);
    }
}
