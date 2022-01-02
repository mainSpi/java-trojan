package greek.horse.client;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import greek.horse.models.MessageType;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class ChatUI extends JFrame {
    private final JTextField messageTextField;
    private final JButton sendBtn;
    private final AbstractDocument doc;
    private static final SimpleAttributeSet serverAttr = new SimpleAttributeSet();
    private static final SimpleAttributeSet clientAttr = new SimpleAttributeSet();
    private static final SimpleAttributeSet defaultAttr = new SimpleAttributeSet();

    private static final Logger log = Logger.getLogger(ChatUI.class);

    static {
        StyleConstants.setFontSize(serverAttr, 16);
        StyleConstants.setForeground(serverAttr, new Color(200, 0, 0));

        StyleConstants.setFontSize(clientAttr, 16);
        StyleConstants.setForeground(clientAttr, Color.BLUE);

        StyleConstants.setFontSize(defaultAttr, 16);
        StyleConstants.setForeground(defaultAttr, Color.BLACK);
    }

    public ChatUI() {
        super("Chat");

        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception e) {
            log.error("Could not set system look and feel", e);
        }
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);

        setBounds(100, 100, 360, 410);
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(mainPanel);
        mainPanel.setLayout(new BorderLayout(0, 5));

        JScrollPane scrollPane = new JScrollPane();
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JTextPane textPane = new JTextPane();
        textPane.setBackground(Color.WHITE);
        textPane.setEditable(false);
        scrollPane.setViewportView(textPane);

        JPanel panel = new JPanel();
        mainPanel.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(5, 0));

        messageTextField = new JTextField();
        messageTextField.setFont(new Font("System", Font.PLAIN, 15));
        panel.add(messageTextField, BorderLayout.CENTER);
        messageTextField.setColumns(10);

        sendBtn = new JButton("Send");
        sendBtn.setFont(new Font("System", Font.PLAIN, 15));
        panel.add(sendBtn, BorderLayout.EAST);

        this.setContentPane(mainPanel);
//        this.setSize(new Dimension(360, 410));
//        this.setMaximumSize(this.getSize());
        this.setResizable(false);
        this.doc = (AbstractDocument) textPane.getStyledDocument();
        this.messageTextField.setFocusable(true);
        this.messageTextField.grabFocus();

        textPane.setFocusable(false);
        DefaultCaret caret = (DefaultCaret) textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);

        this.setLocationByPlatform(false);
        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        int x = ((int) bounds.getWidth() / 2) - (this.getWidth() / 2);
        int y = ((int) bounds.getHeight() / 3) - (this.getHeight() / 3);
        if (x > 0 && y > 0) {
            this.setLocation(x, y);
        } else {
            this.setLocation(0, 0);
        }

        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                setState(JFrame.MAXIMIZED_BOTH);
                toFront();
            }
        });
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {
                setState(JFrame.MAXIMIZED_BOTH);
                toFront();
            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                setState(JFrame.MAXIMIZED_BOTH);
                toFront();
            }
        });


    }

    public static void main(String[] args) throws BadLocationException, InterruptedException {
        FlatLightLaf.setup();
        ChatUI frame = new ChatUI();

        for (int i = 0; i < 15; i++) {
            frame.addLine("Hacker", "se fudeo", MessageType.SERVER);
            frame.addLine("sofredor", "oh nao daddy", MessageType.CLIENT);
        }

        frame.setSendListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getActionCommand());
            }
        });

        frame.setVisible(true);

    }

    public void addLine(String name, String message, MessageType type) {
        try {
            this.doc.insertString(doc.getLength(), name, type.equals(MessageType.SERVER) ? serverAttr : clientAttr);
            this.doc.insertString(doc.getLength(), " > " + message + "\n", defaultAttr);

        } catch (Exception e) {
            log.error("Error writing to TextPane", e);
        }

    }

    public void setSendListener(ActionListener listener) {
        this.sendBtn.addActionListener(e -> actionMessage(listener, e.getSource(), e.getID()));
        this.messageTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    actionMessage(listener, e.getSource(), e.getID());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    public void removeSendListener() {
        Arrays.asList(this.sendBtn.getActionListeners()).forEach(sendBtn::removeActionListener);
        Arrays.asList(this.messageTextField.getKeyListeners()).forEach(messageTextField::removeKeyListener);
    }

    public void cleanMessages() {
        try {
            this.doc.remove(0, doc.getLength());
        } catch (Exception e) {
            log.error("Error cleaning doc", e);
        }
    }

    private void actionMessage(ActionListener listener, Object source, int id) {
        listener.actionPerformed(new ActionEvent(source, id, messageTextField.getText()));
        messageTextField.setText("");
    }

}
