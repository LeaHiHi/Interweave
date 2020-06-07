package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.Level;

import static jdk.nashorn.internal.objects.NativeMath.log;

// Thanks https://github.com/DV8FromTheWorld/Yui/blob/e8da929a8f637591e4da53599c39c8161be38746/src/net/dv8tion//SettingsManager.java
public class SettingsManager {
    private static SettingsManager instance;
    public final ObjectMapper om = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private Settings settings;
    public final Path configFile = new File(".").toPath().resolve("InterweaveConfig.json");

    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    public SettingsManager() {
        if (!configFile.toFile().exists()) {
            log(Level.INFO,"Creating default Settings");
            log(Level.INFO,"You will need to edit the InterweaveConfig.json with your login information.");
            this.settings = getDefaultSettings();
            try {
                saveSettings();
            } catch (Exception e) {
                log(Level.ERROR,"Error writing default settings!");
            }
            System.exit(1);
        }
        loadSettings();
    }

    public void loadSettings() {
        try {
            this.settings = om.readValue(configFile.toFile(), Settings.class);
            log(Level.INFO, "Settings loaded");
        } catch (Exception e) {
            log(Level.ERROR,"Error Loading Settings");
        }
    }

    public Settings getSettings() {
        return settings;
    }

    public void saveSettings() throws IOException {
        om.writeValue(configFile.toFile(), this.settings);
    }

    private Settings getDefaultSettings() {
        Settings defaultSettings = new Settings();
        defaultSettings.setDiscordToken("Bot token here...");
        defaultSettings.setChannelId("Channel Id here...");
        defaultSettings.setAnnouncementFormat("***%message%***");
        defaultSettings.setChatFormat("**<%sender%>** %message%");
        defaultSettings.setDeathFormat(":skull_crossbones: **%message%**");
        defaultSettings.setJoinFormat(":small_red_triangle: **%message%**");
        defaultSettings.setLeaveFormat(":small_red_triangle_down: **%message%**");
        defaultSettings.setStartFormat(":small_blue_diamond: **Server has started!**");
        defaultSettings.setStopFormat(":small_orange_diamond: **Server has stopped!**");
        defaultSettings.setAdvancementFormat(":medal: **%message%**");
        defaultSettings.setDiscordToMinecraftFormat("§b§l<%sender%>§r %message%");
        defaultSettings.setEmoteFormat("*%sender% %message%*");
        defaultSettings.setDingPlayersIfNameFound(true);
        return defaultSettings;
    }
}


