package me.dustin.chatbot.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.dustin.chatbot.ChatBot;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class GeneralHelper {


    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final String ANSI_RESET = "\u001B[0m";

    public static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    public static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void print(String s, TextColors color) {
        if (ChatBot.getGui() != null) {
            try {
                StyledDocument document = ChatBot.getGui().getOutput().getStyledDocument();
                document.insertString(document.getLength(), s + "\n", ChatBot.getConfig().isColorConsole() ? color.getStyle() : null);
                ChatBot.getGui().getOutput().setCaretPosition(ChatBot.getGui().getOutput().getDocument().getLength());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ChatBot.getConfig() != null && ChatBot.getConfig().isColorConsole()) {
            System.out.println(color.getAnsi() + s + ANSI_RESET);
        } else {
            System.out.println(s);
        }
    }

    public static String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String inString;
        while ((inString = in.readLine()) != null) {
            sb.append(inString);
            sb.append("\n");
        }
        in.close();
        return sb.toString();
    }

    public static String getDurationString(long ms) {
        if (ms <= 0) {
            return "-";
        }

        long days = TimeUnit.MILLISECONDS.toDays(ms);
        ms -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(ms);
        ms -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(ms);
        ms -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(ms);

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days);
            sb.append("d ");
        }
        if (hours > 0) {
            String s = String.format("%02d", hours);
            if (s.startsWith("0"))
                s = s.substring(1);
            sb.append(s);
            sb.append("h ");
        }
        if (minutes > 0) {
            String s = String.format("%02d", minutes);
            if (s.startsWith("0"))
                s = s.substring(1);
            sb.append(s);
            sb.append("min ");
        }
        if (seconds > 0) {
            String s = String.format("%02d", seconds);
            if (s.startsWith("0"))
                s = s.substring(1);
            sb.append(s);
            sb.append("s");
        }

        return sb.toString();
    }

    public static HttpResponse httpRequest(String url, Object data, Map<String, String> headers, String requestMethod) {
        try {
            URL theURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) theURL.openConnection();
            connection.setRequestMethod(requestMethod);
            connection.setConnectTimeout(10 * 1000);
            connection.setDoInput(true);
            if (headers != null)
                headers.forEach(connection::setRequestProperty);
            if (data != null) {
                connection.setDoOutput(true);
                byte[] bytes = new byte[0];
                if (data instanceof Map<?, ?> m) {
                    String encoded = encode((Map<Object, Object>) m);
                    bytes = encoded.getBytes();
                } else if (data instanceof String s) {
                    bytes = s.getBytes();
                }
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(bytes);
                }
            }
            StringBuilder sb = new StringBuilder();
            int code = connection.getResponseCode();
            if (code >= 200 && code < 300) {
                BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                for (String line; (line = input.readLine()) != null; ) {
                    sb.append(line);
                    sb.append("\n");
                }
            }
            connection.disconnect();
            return new HttpResponse(sb.toString(), code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HttpResponse("", 404);
    }

    private static String encode(Map<Object, Object> map) {
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<?, ?> entry : map.entrySet())
            sj.add(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        return sj.toString();
    }

    public static Authenticator getAuth(String user, String password) {
       return new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return (new PasswordAuthentication(user, password.toCharArray()));
            }
        };
    }

    public static void initTextColors(StyledDocument document) {
        Style black = document.addStyle("black", null);
        StyleConstants.setForeground(black, Color.BLACK);
        TextColors.BLACK.setStyle(black);

        Style red = document.addStyle("red", null);
        StyleConstants.setForeground(red, Color.RED);
        TextColors.RED.setStyle(red);

        Style green = document.addStyle("green", null);
        StyleConstants.setForeground(green, Color.GREEN);
        TextColors.GREEN.setStyle(green);

        Style yellow = document.addStyle("yellow", null);
        StyleConstants.setForeground(yellow, Color.YELLOW);
        TextColors.YELLOW.setStyle(yellow);

        Style blue = document.addStyle("blue", null);
        StyleConstants.setForeground(blue, Color.BLUE);
        TextColors.BLUE.setStyle(blue);

        Style purple = document.addStyle("purple", null);
        StyleConstants.setForeground(purple, Color.MAGENTA);
        TextColors.PURPLE.setStyle(purple);

        Style cyan = document.addStyle("cyan", null);
        StyleConstants.setForeground(cyan, Color.CYAN);
        TextColors.CYAN.setStyle(cyan);

        Style white = document.addStyle("white", null);
        StyleConstants.setForeground(white, Color.WHITE);
        TextColors.WHITE.setStyle(white);
    }

    public record HttpResponse(String data, int responseCode){}

    public enum TextColors {
        BLACK("\u001B[30m", null),
        RED("\u001B[31m", null),
        GREEN("\u001B[32m", null),
        YELLOW("\u001B[33m", null),
        BLUE("\u001B[34m", null),
        PURPLE("\u001B[35m", null),
        CYAN("\u001B[36m", null),
        WHITE("\u001B[37m", null);

        private final String ansi;
        private Style style;
        TextColors(String ansi, Style style) {
            this.ansi = ansi;
            this.style = style;
        }

        public String getAnsi() {
            return ansi;
        }

        public Style getStyle() {
            return style;
        }

        public void setStyle(Style style) {
            this.style = style;
        }
    }
}
