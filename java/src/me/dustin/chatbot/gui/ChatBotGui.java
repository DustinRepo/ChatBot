package me.dustin.chatbot.gui;

import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.event.EventAddPlayer;
import me.dustin.chatbot.event.EventRemovePlayer;
import me.dustin.chatbot.network.ClientConnection;
import me.dustin.chatbot.network.packet.c2s.play.ServerBoundChatPacket;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatBotGui extends JFrame {
    private final  CustomTextPane output;
    private final  JTextField input;
    private final DefaultListModel<String> model;

    public ChatBotGui() {
        JButton sendButton = new JButton();
        this.output = new CustomTextPane(true);
        this.input = new JTextField();
        JList<String> playerList = new JList<>();
        JScrollPane outputScrollPane = new JScrollPane(output);
        JScrollPane playerListScrollPane = new JScrollPane(playerList);
        boolean linux = System.getProperty("os.name").toLowerCase().contains("linux");
        if (!linux) {
            outputScrollPane.setBounds(1, 1, 600, 550);
            playerListScrollPane.setBounds(601, 1, 182, 550);
            input.setBounds(1, 555, 600, 30);
            sendButton.setBounds(601, 555, 182, 30);
        } else {
            outputScrollPane.setBounds(1, 1, 600, 550);
            playerListScrollPane.setBounds(601, 1, 193, 550);
            input.setBounds(1, 555, 600, 35);
            sendButton.setBounds(601, 555, 193, 35);
        }
        sendButton.setText("Send");
        output.setEditable(false);
        this.setTitle("ChatBot");
        this.setSize(800, linux ? 620 : 625);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.getContentPane().setLayout(null);
        this.getContentPane().add(outputScrollPane);
        this.getContentPane().add(playerListScrollPane);
        this.getContentPane().add(this.input);
        this.getContentPane().add(sendButton);

        this.output.setText("");
        model = new DefaultListModel<>();
        playerList.setModel(model);

        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {//they pressed enter
                    if (ChatBot.getClientConnection() != null)
                        ChatBot.getClientConnection().sendPacket(new ServerBoundChatPacket(input.getText()));
                    input.setText("");
                }
            }
        });
        sendButton.addActionListener(actionEvent -> {
            if (ChatBot.getClientConnection() != null)
                ChatBot.getClientConnection().sendPacket(new ServerBoundChatPacket(input.getText()));
            this.input.setText("");
        });
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (ChatBot.getClientConnection() != null)
                    ChatBot.getClientConnection().getProcessManager().stopAll();
                System.exit(0);
            }
        });
        new Timer(50, e -> {
            if (ChatBot.getClientConnection() != null && ChatBot.getClientConnection().getNetworkState() == ClientConnection.NetworkState.PLAY) {
                tick();
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
        if (ChatBot.getClientConnection() != null && ChatBot.getClientConnection().isConnected())
            setTitle("ChatBot - " + ChatBot.getClientConnection().getMinecraftServerAddress().getIp() + ":" + ChatBot.getClientConnection().getMinecraftServerAddress().getPort());
    }

    public void updateComponents() {
        UIDefaults defs = UIManager.getDefaults();
        defs.put("TextPane.background", new ColorUIResource(new Color(60, 60, 60)));
        defs.put("TextPane.inactiveBackground", new ColorUIResource(new Color(60, 60, 60)));
        defs.put("List.background", new ColorUIResource(new Color(60, 60, 60)));
        defs.put("List.foreground", new ColorUIResource(Color.WHITE));
        defs.put("TextField.background", new ColorUIResource(new Color(60, 60, 60)));
        defs.put("TextField.foreground", new ColorUIResource(Color.WHITE));
        getContentPane().setBackground(new Color(30, 30, 30));
        SwingUtilities.updateComponentTreeUI(this);
    }

    public JTextPane getOutput() {
        return output;
    }

    public DefaultListModel<String> getPlayerList() {
        return model;
    }

}
