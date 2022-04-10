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

public class ChatBotGui {
    private final JFrame frame;
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
        outputScrollPane.setBounds(1, 1, 600, 550);
        playerListScrollPane.setBounds(601, 1, 195, 550);
        input.setBounds(1, 555, 600, 35);
        sendButton.setBounds(601, 555, 195, 35);
        sendButton.setText("Send");
        output.setEditable(false);
        this.frame = new JFrame("ChatBot");
        this.frame.setSize(800, 625);
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
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (ChatBot.getClientConnection() != null)
                    ChatBot.getClientConnection().getProcessManager().stopAll();
                System.exit(0);
            }
        });
        new Timer(50, e -> {
            if (ChatBot.getClientConnection() != null && ChatBot.getClientConnection().getNetworkState() == ClientConnection.NetworkState.PLAY) {
                ChatBot.getClientConnection().tick();
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
            frame.setTitle("ChatBot - " + ChatBot.getClientConnection().getMinecraftServerAddress().getIp() + ":" + ChatBot.getClientConnection().getMinecraftServerAddress().getPort());
    }

    public JFrame getFrame() {
        return frame;
    }

    public void updateComponents() {
        if (!System.getProperty("os.name").toLowerCase().contains("linux") && ChatBot.getConfig().isColorConsole()) {
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

}
