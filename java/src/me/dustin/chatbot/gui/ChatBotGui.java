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
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatBotGui {
    private final JFrame frame;
    private final  CustomTextPane output;
    private final  JTextField input;
    private final DefaultListModel<String> model;

    private ClientConnection clientConnection;
    private final StopWatch stopWatch = new StopWatch();

    public ChatBotGui() {
        JButton sendButton = new JButton();
        this.output = new CustomTextPane(true);
        this.input = new JTextField();
        JList<String> playerList = new JList<>();
        JScrollPane outputScrollPane = new JScrollPane(output);
        JScrollPane playerListScrollPane = new JScrollPane(playerList);
        outputScrollPane.setBounds(1, 1, 600, 550);
        playerListScrollPane.setBounds(601, 1, 198, 550);
        input.setBounds(1, 551, 600, 50);
        sendButton.setBounds(601, 551, 198, 50);
        sendButton.setText("Send");
        output.setEditable(false);
        this.frame = new JFrame("ChatBot");
        this.frame.setSize(800, 630);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setVisible(true);
        this.frame.setResizable(false);
        this.frame.setLocationRelativeTo(null);
        this.frame.getContentPane().setLayout(null);
        this.frame.getContentPane().add(outputScrollPane);
        this.frame.getContentPane().add(playerListScrollPane);
        this.frame.getContentPane().add(this.input);
        this.frame.getContentPane().add(sendButton);

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
        if (clientConnection == null || !clientConnection.isConnected())
            frame.setTitle("ChatBot - Disconnected");
        else
            frame.setTitle("ChatBot - Connected to: " + clientConnection.getIp() + ":" + clientConnection.getPort() + " for: " + GeneralHelper.getDurationString(ChatBot.connectionTime()));
    }

    public JFrame getFrame() {
        return frame;
    }

    public void updateComponents() {
        if (System.getProperty("os.name").toLowerCase().contains("win") && ChatBot.getConfig().isColorConsole()) {
            UIDefaults defs = UIManager.getDefaults();
            defs.put("TextPane.background", new ColorUIResource(new Color(60, 60, 60)));
            defs.put("TextPane.inactiveBackground", new ColorUIResource(new Color(60, 60, 60)));
            defs.put("List.background", new ColorUIResource(new Color(60, 60, 60)));
            defs.put("List.foreground", new ColorUIResource(Color.WHITE));
            defs.put("TextField.background", new ColorUIResource(new Color(60, 60, 60)));
            defs.put("TextField.foreground", new ColorUIResource(Color.WHITE));
        }
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
