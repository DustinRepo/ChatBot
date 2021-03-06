package me.dustin.chatbot.account.login;

import com.google.gson.JsonObject;
import me.dustin.chatbot.account.MinecraftAccount;
import me.dustin.chatbot.account.Session;
import me.dustin.chatbot.helper.GeneralHelper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MSLoginHelper {

    private final static HashMap<String, String> defaultHeader = new HashMap<>();
    private final static HashMap<String, String> jsonHeader = new HashMap<>();

    private final MinecraftAccount.MicrosoftAccount microsoftAccount;

    static {
        defaultHeader.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        jsonHeader.put("Content-Type", "application/json");
        jsonHeader.put("Accept", "application/json");
    }

    public MSLoginHelper(MinecraftAccount.MicrosoftAccount microsoftAccount) {
        this.microsoftAccount = microsoftAccount;
    }

    public Session login(Consumer<String> statusConsumer) {
        try {
            statusConsumer.accept("Getting login code");
            String loginCode = getLoginCode();
            if (loginCode == null) {
                statusConsumer.accept("Error grabbing login code");
                return null;
            }
            //get access token
            statusConsumer.accept("Getting access_token");
            String[] tokenResponse = getAccessTokens(loginCode);
            if (tokenResponse == null) {
                statusConsumer.accept("Error grabbing access token");
                return null;
            }
            String accessToken = tokenResponse[0];
            String refreshToken = tokenResponse[1];
            //authenticate with Xbox Live
            statusConsumer.accept("Authenticating XBL");
            String[] xblResponse = xblAuthenticate(accessToken);
            if (xblResponse == null) {
                statusConsumer.accept("Error authenticating with Xbox Live");
                return null;
            }
            String xblToken = xblResponse[0];
            String xbluserhash = xblResponse[1];
            //xsts authenticate
            statusConsumer.accept("Authenticating XSTS");
            String[] xstsResponse = xstsAuthenticate(xblToken);
            if (xstsResponse == null) {
                statusConsumer.accept("Error authenticating with Xbox Live");
                return null;
            }
            String xstsToken = xstsResponse[0];
            String xstsuserhash = xstsResponse[1];

            //minecraft authenticate
            statusConsumer.accept("Authenticating Minecraft");
            String bearerToken = minecraftAuthenticate(xstsToken, xstsuserhash);
            if (bearerToken == null) {
                statusConsumer.accept("Error authenticating with Minecraft");
                return null;
            }
            statusConsumer.accept("Authenticating MS Store");
            if (!msStoreAuthenticate(bearerToken)) {
                statusConsumer.accept("Error authenticating with MS Store");
                return null;
            }
            statusConsumer.accept("Grabbing Minecraft profile");
            String[] profileData = getProfile(bearerToken);
            if (profileData == null) {
                statusConsumer.accept("Error grabbing Minecraft profile");
                return null;
            }
            statusConsumer.accept("Login success");
            String name = profileData[0];
            String uuid = profileData[1];

            return new Session(name, uuid, bearerToken, Session.AccountType.MICROSOFT);
        } catch (Exception e) {
            e.printStackTrace();
            statusConsumer.accept(e.getMessage());
        }
        return null;
    }

    private String[] getAccessTokens(String loginCode) {
        try {
            String url = "https://login.live.com/oauth20_token.srf";
            Map<String, String> accessTokenData = new HashMap<>();
            accessTokenData.put("client_id", "00000000402b5328");
            accessTokenData.put("code", loginCode);
            accessTokenData.put("grant_type", "authorization_code");
            accessTokenData.put("redirect_uri", "https://login.live.com/oauth20_desktop.srf");
            accessTokenData.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");

            GeneralHelper.HttpResponse authTokenResponse = GeneralHelper.httpRequest(url, accessTokenData, defaultHeader, "POST");
            JsonObject jsonObject = GeneralHelper.gson.fromJson(authTokenResponse.data(), JsonObject.class);
            return new String[] {jsonObject.get("access_token").getAsString(), jsonObject.get("refresh_token").getAsString()};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String[] xblAuthenticate(String accessToken) {
        try {
            String url = "https://user.auth.xboxlive.com/user/authenticate";
            Map<String, String> properties = new HashMap<>();
            properties.put("AuthMethod", "RPS");
            properties.put("SiteName", "user.auth.xboxlive.com");
            properties.put("RpsTicket", accessToken);
            Map<String, Object> xblAuthData = new HashMap<>();
            xblAuthData.put("Properties", properties);
            xblAuthData.put("RelyingParty", "http://auth.xboxlive.com");
            xblAuthData.put("TokenType", "JWT");

            GeneralHelper.HttpResponse httpResponse = GeneralHelper.httpRequest(url, GeneralHelper.gson.toJson(xblAuthData), jsonHeader, "POST");
            JsonObject object = GeneralHelper.gson.fromJson(httpResponse.data(), JsonObject.class);
            return new String[]{object.get("Token").getAsString(), object.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString()};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String[] xstsAuthenticate(String token) {
        try {
            String url = "https://xsts.auth.xboxlive.com/xsts/authorize";
            Map<String, Object> properties = new HashMap<>();
            properties.put("SandboxId", "RETAIL");
            properties.put("UserTokens", List.of(token));
            Map<String, Object> xblAuthData = new HashMap<>();
            xblAuthData.put("Properties", properties);
            xblAuthData.put("RelyingParty", "rp://api.minecraftservices.com/");
            xblAuthData.put("TokenType", "JWT");

            GeneralHelper.HttpResponse httpResponse = GeneralHelper.httpRequest(url, GeneralHelper.gson.toJson(xblAuthData), jsonHeader, "POST");
            JsonObject object = GeneralHelper.gson.fromJson(httpResponse.data(), JsonObject.class);
            return new String[]{object.get("Token").getAsString(), object.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString()};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String minecraftAuthenticate(String xstsToken, String userhash) {
        try {
            String url = "https://api.minecraftservices.com/authentication/login_with_xbox";
            Map<String, String> mcAuthData = new HashMap<>();
            mcAuthData.put("identityToken", String.format("XBL3.0 x=%s;%s", userhash, xstsToken));
            GeneralHelper.HttpResponse httpResponse = GeneralHelper.httpRequest(url, GeneralHelper.gson.toJson(mcAuthData), jsonHeader, "POST");
            JsonObject object = GeneralHelper.gson.fromJson(httpResponse.data(), JsonObject.class);
            return object.get("access_token").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean msStoreAuthenticate(String bearerToken) {
        try {
            String url = "https://api.minecraftservices.com/entitlements/mcstore";
            Map<String, String> header = new HashMap<>();
            header.put("Authorization", "Bearer " + bearerToken);
            GeneralHelper.HttpResponse httpResponse = GeneralHelper.httpRequest(url, null, header, "GET");
            return httpResponse.data().contains("minecraft");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String[] getProfile(String bearerToken) {
        try {
            String url = "https://api.minecraftservices.com/minecraft/profile";
            Map<String, String> header = new HashMap<>();
            header.put("Authorization", "Bearer " + bearerToken);
            GeneralHelper.HttpResponse httpResponse = GeneralHelper.httpRequest(url, null, header, "GET");
            JsonObject object = GeneralHelper.gson.fromJson(httpResponse.data(), JsonObject.class);
            return new String[]{object.get("name").getAsString(), object.get("id").getAsString()};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getLoginCode() {
        try {
            String url = "https://login.live.com/oauth20_authorize.srf?redirect_uri=https://login.live.com/oauth20_desktop.srf&scope=service::user.auth.xboxlive.com::MBI_SSL&display=touch&response_type=code&locale=en&client_id=00000000402b5328";
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            InputStream inputStream = httpURLConnection.getResponseCode() == 200 ? httpURLConnection.getInputStream() : httpURLConnection.getErrorStream();

            String loginCookie = httpURLConnection.getHeaderField("set-cookie");

            String responseData = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());
            Matcher bodyMatcher = Pattern.compile("sFTTag:[ ]?'.*value=\"(.*)\"/>'").matcher(responseData);
            String loginPPFT;
            if (bodyMatcher.find())
                loginPPFT = bodyMatcher.group(1);
            else return null;

            String loginUrl;
            bodyMatcher = Pattern.compile("urlPost:[ ]?'(.+?(?='))").matcher(responseData);
            if (bodyMatcher.find())
                loginUrl = bodyMatcher.group(1);
            else return null;

            if (loginCookie == null || loginPPFT == null || loginUrl == null)
                return null;

            Map<String, String> requestData = new HashMap<>();
            requestData.put("login", microsoftAccount.getEmail());
            requestData.put("loginfmt", microsoftAccount.getEmail());
            requestData.put("passwd", microsoftAccount.getPassword());
            requestData.put("PPFT", loginPPFT);

            StringJoiner postData = new StringJoiner("&");
            for (Map.Entry<?, ?> entry : requestData.entrySet())
                postData.add(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));

            byte[] data = postData.toString().getBytes();
            HttpURLConnection connection = (HttpURLConnection) new URL(loginUrl).openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));
            connection.setRequestProperty("Cookie", loginCookie);
            connection.setConnectTimeout(10 * 1000);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(data);
            }

            if (connection.getResponseCode() != 200 || connection.getURL().toString().equals(loginUrl))
                return null;

            Pattern pattern = Pattern.compile("[?|&]code=([\\w.-]+)");

            Matcher tokenMatcher = pattern.matcher(URLDecoder.decode(connection.getURL().toString(), StandardCharsets.UTF_8));
            if (tokenMatcher.find()) {
                return tokenMatcher.group(1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MinecraftAccount.MicrosoftAccount getAccount() {
        return microsoftAccount;
    }
}
