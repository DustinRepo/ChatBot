package me.dustin.chatbot.gui;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.chat.ChatMessage;
import me.dustin.chatbot.event.EventAddPlayer;
import me.dustin.chatbot.event.EventRemovePlayer;
import me.dustin.chatbot.helper.GeneralHelper;
import me.dustin.chatbot.helper.StopWatch;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatBotGui {
    private JFrame frame;
    private JPanel panel;
    private JButton sendButton;
    private JTextPane output;
    private JTextField input;
    private JList<String> playerList;
    private DefaultListModel<String> model;

    private ClientConnection clientConnection;
    private final StopWatch stopWatch = new StopWatch();

    public ChatBotGui() {
        this.frame = new JFrame("ChatBot");
        this.frame.setSize(800, 600);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setVisible(true);
        this.frame.setResizable(true);
        this.frame.setLocationRelativeTo(null);
        this.frame.add(this.panel);
        this.output.setText("");

        model = new DefaultListModel<>();
        playerList.setModel(model);

        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {//they pressed enter
                    if (clientConnection != null)
                        clientConnection.sendPacket(new ServerBoundChatPacket(input.getText()));
                    input.setText("");
                }
            }
        });
        sendButton.addActionListener(actionEvent -> {
            if (clientConnection != null)
                this.clientConnection.sendPacket(new ServerBoundChatPacket(input.getText()));
            this.input.setText("");
        });
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (clientConnection != null)
                    clientConnection.getProcessManager().stopAll();
                System.exit(0);
            }
        });
        new Timer(1000, e -> {
            if (clientConnection != null && clientConnection.getNetworkState() == ClientConnection.NetworkState.PLAY) {
                if (stopWatch.hasPassed(ChatBot.getConfig().getKeepAliveCheckTime() * 1000L)) {
                    GeneralHelper.print("Thread stopped responding, closing connection...", ChatMessage.TextColors.DARK_RED);
                    clientConnection.close();
                    clientConnection = null;
                    stopWatch.reset();
                }
            } else {
                stopWatch.reset();
            }
        }).start();
        setLookAndFeel();
    }

    @EventPointer
    private final EventListener<EventAddPlayer> eventAddPlayerEventListener = new EventListener<>(event -> {
        getPlayerList().addElement(event.getPlayer().getName());
    });

    @EventPointer
    private final EventListener<EventRemovePlayer> eventRemovePlayerEventListener = new EventListener<>(event -> {
        getPlayerList().removeElement(event.getPlayer().getName());
    });

    public void tick() {
        stopWatch.reset();
        frame.setTitle("ChatBot - Connected to: " + clientConnection.getIp() + ":" + clientConnection.getPort() + " for: " + GeneralHelper.getDurationString(ChatBot.connectionTime()));
    }

    public void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            updateComponents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JFrame getFrame() {
        return frame;
    }

    public void updateComponents() {
        SwingUtilities.updateComponentTreeUI(frame);
    }

    public JTextPane getOutput() {
        return output;
    }

    public DefaultListModel<String> getPlayerList() {
        return model;
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }
}
