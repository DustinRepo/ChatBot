package me.dustin.chatbot.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.dustin.chatbot.ChatBot;
import me.dustin.chatbot.chat.ChatMessage;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import java.util.logging.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralHelper {


    public static final String ANSI_RESET = "\u001B[0m";
    private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");

    public static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    public static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static Logger logger;

    public static void print(String s, ChatMessage.TextColors color) {
        String timeStampString = getCurrentTimeStamp();
        timeStampString = String.format("[%s] ", timeStampString);
        if (ChatBot.getGui() != null) {
            try {
                StyledDocument document = ChatBot.getGui().getOutput().getStyledDocument();
                document.insertString(document.getLength(), timeStampString, ChatMessage.TextColors.GRAY.getStyle());
                document.insertString(document.getLength(), s + "\n", ChatBot.getConfig().isColorConsole() ? color.getStyle() : null);

                ChatBot.getGui().getOutput().setCaretPosition(ChatBot.getGui().getOutput().getDocument().getLength());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ChatBot.getConfig().isColorConsole()) {
            System.out.println(timeStampString + color.getAnsi() + s + ANSI_RESET);
        } else {
            System.out.println(timeStampString + strip(s));
        }
        if (logger != null)
            logger.info(s);
    }

    public static void printChat(ChatMessage chatMessage) {
        if (!chatMessage.getSenderName().isEmpty() && !chatMessage.getSenderName().startsWith("<"))
            chatMessage = new ChatMessage("<" + chatMessage.getSenderName() + (chatMessage.getSenderName().contains("§") ? "§f" : "") +">", chatMessage.getBody());
        String m = chatMessage.getMessage();
        if (!m.contains("§") || !ChatBot.getConfig().isColorConsole()) {
            print(m, ChatMessage.TextColors.WHITE);
            return;
        }
        printColorText(chatMessage.getMessage());
        if (logger != null)
            logger.info(chatMessage.getMessage());
    }

    private static void printColorText(String text) {
        ChatMessage.TextColors color;
        StyledDocument document = ChatBot.getGui() != null ? ChatBot.getGui().getOutput().getStyledDocument() : null;
        String timeStampString = String.format("[%s] ", getCurrentTimeStamp());
        try {
            if (document != null)
                document.insertString(document.getLength(), timeStampString, ChatMessage.TextColors.GRAY.getStyle());
            System.out.print(ANSI_RESET + timeStampString);
            for (String s : text.split("§")) {
                if (s.length() == 0)
                    continue;
                color = ChatMessage.TextColors.getFromChar(s.charAt(0));
                String s1 = color == null ? s : s.substring(1);
                if (color == null)
                    color = ChatMessage.TextColors.WHITE;
                if (document != null)
                    document.insertString(document.getLength(), s1, color.getStyle());
                System.out.print(color.getAnsi() + s1 + ANSI_RESET);
            }
            if (document != null) {
                document.insertString(document.getLength(), "\n", ChatMessage.TextColors.WHITE.getStyle());
                ChatBot.getGui().getOutput().setCaretPosition(ChatBot.getGui().getOutput().getDocument().getLength());
            }
            System.out.print(ANSI_RESET + "\n");
        } catch (Exception e) {}
    }

    public static String strip(String string) {
        return string == null ? null : FORMATTING_CODE_PATTERN.matcher(string).replaceAll("");
    }

    public static boolean matchUUIDs(String s, String s1) {
        return s.replace("-", "").equalsIgnoreCase(s1.replace("-", ""));
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        return sdfDate.format(now);
    }

    public static String getCurrentTimeStamp(long ms) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date(ms);
        return sdfDate.format(now);
    }

    public static int countMatches(String in, String search) {
        Matcher m = Pattern.compile(search).matcher(in);
        int i = 0;
        while (m.find()) {
            i++;
        }
        return i;
    }

    public static String readFile(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String inString;
            while ((inString = in.readLine()) != null) {
                sb.append(inString);
                sb.append("\n");
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void writeFile(File file, java.util.List<String> content) {
        try {
            PrintWriter printWriter = new PrintWriter(file);
            StringBuilder stringBuilder = new StringBuilder();
            content.forEach(string -> {
                stringBuilder.append(string + "\r\n");
            });
            printWriter.print(stringBuilder);
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static void initLogger() {
        try {
            logger = Logger.getLogger("ChatBot");
            File logsFolder = new File(new File("").getAbsolutePath(), "logs");
            if (!logsFolder.exists())
                logsFolder.mkdirs();
            EasyFormatter easyFormatter = new EasyFormatter();
            FileHandler fileHandler = new FileHandler(new File(logsFolder, getCurrentTimeStamp() + ".txt").getAbsolutePath(), true);
            fileHandler.setFormatter(easyFormatter);

            logger.setUseParentHandlers(false);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static UUID uuidFromStringNoDashes(String uuid) {
        return UUID.fromString(uuid.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
        ));
    }

    public record HttpResponse(String data, int responseCode){}

    static class EasyFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();
            sb.append(getCurrentTimeStamp(record.getMillis())).append(':');;
            sb.append(record.getMessage()).append('\n');
            return sb.toString();
        }
    }
}
