package main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.DiscordListener;
import utils.Settings;
import utils.SettingsManager;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Interweave implements DedicatedServerModInitializer {

    private static Logger LOGGER = LogManager.getLogger();
    private static Settings settings;

    public static JDA jda;

    public static final String MOD_ID = "interweave";
    public static final String MOD_NAME = "Interweave";

    public static HashMap<String, String> mentionables;

    private static int tick = 0;
    private static int users = 0;

    @Override
    public void onInitializeServer()  {
        settings = SettingsManager.getInstance().getSettings();

        //JDA
        try {
            initJDA(settings);
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        ServerTickCallback.EVENT.register(server -> {
            tick++;
            users++;
            if (tick % 240 == 0) {
                if (users >= server.getMaxPlayerCount()) {
                    users = 0;
                }
                jda.getPresence().setActivity(Activity.of(Activity.ActivityType.DEFAULT, server.getCurrentPlayerCount() + "/" + server.getMaxPlayerCount() + " | " + server.getPlayerNames()[users]));
                if (tick == 1201) {
                    tick = 0;
                }
            }
        });
    }

    private void initJDA(Settings settings) throws LoginException, InterruptedException {
        if (settings.getDiscordToken() == null || settings.getDiscordToken().equals("Bot token here...")) {
            log(Level.ERROR,"The Discord token is not configured correctly! The bot will now exit. Please check your InterweaveConfig.json file.");
            System.exit(1);
        }
        if (settings.getChannelId() == null || settings.getChannelId().equals("Channel Id here...")) {
            log(Level.ERROR, "The Channel Id is not configured correctly! The bot will now exit. Please check your InterweaveConfig.json file.");
            System.exit(1);
        }

        jda = JDABuilder.createLight(settings.getDiscordToken()).enableIntents(GatewayIntent.GUILD_MEMBERS).build();

        jda.addEventListener(new DiscordListener());

        jda.awaitReady();
        mentionables = buildDiscordMentionables();
    }

    public static void sendMessage(Text msg) {
        //%message% message
        //%sender% sender
        if (!(msg instanceof TranslatableText)) {
            return;
        }
        String key = ((TranslatableText) msg).getKey();
        String message = msg.getString();
        //admin command
        if (key.equals("chat.type.admin")) {
            return;
        }
        //server control
        if (key.contains("commands")) {
            return;
        }
        //advancement
        if (key.contains("chat.type.advancement")) {
            if (settings.getAdvancementFormat() == null) {
                return;
            }
            message = settings.getAnnouncementFormat().replace("%message%", message).replace("[","").replace("]","").replace("(","").replace(")","");
        }
        //Player Joined
        if (key.equals("multiplayer.player.joined")) {
            if (settings.getJoinFormat() == null) {
                return;
            }
            message = settings.getJoinFormat().replace("%message%", message);
        }
        //Player Left
        if (key.equals("multiplayer.player.left")) {
            if (settings.getLeaveFormat() == null) {
                return;
            }
            message = settings.getLeaveFormat().replace("%message%", message);
        }
        // /Say
        if (key.equals("chat.type.announcement")) {
            if (settings.getAnnouncementFormat() == null) {
                return;
            }
            message = settings.getAnnouncementFormat().replace("%message%", message);
        }
        //Death
        if (key.contains("death")) {
            if (settings.getDeathFormat() == null) {
                return;
            }
            message = settings.getDeathFormat().replace("%message%", message);
        }
        // /me Chat
        if (key.equals("chat.type.emote")) {
            if (settings.getEmoteFormat() == null) {
                return;
            }
            message = settings.getEmoteFormat().replace("%sender%", ((LiteralText)((TranslatableText) msg).getArgs()[0]).getString()).replace("%message%", ((TranslatableText) msg).getArgs()[1].toString());
        }
        //Regular Chat
        if (key.equals("chat.type.text")) {
            if (settings.getChatFormat() == null) {
                return;
            }
            message = settings.getChatFormat().replace("%sender%", ((LiteralText)((TranslatableText) msg).getArgs()[0]).getString()).replace("%message%", ((TranslatableText) msg).getArgs()[1].toString());
        }
        message = findMention(message);
        try {
            jda.awaitReady();
            jda.getTextChannelById(settings.getChannelId()).sendMessage(message).queue();
        } catch (Exception e) {

        }
    }

    private static String findMention(String message) {
        ArrayList<String> words = new ArrayList<>();
        Collections.addAll(words, message.split(" "));
        StringBuilder builder = new StringBuilder();
        for (String s : words) {
            if (!s.contains("@")) {
                builder.append(s);
            } else {
                String string = s.replace("@", "");
                User user = null;
                if (mentionables.get(string.toLowerCase()) != null) {
                    user = jda.retrieveUserById(mentionables.get(string.toLowerCase())).complete();
                }
                if (user != null) {
                    builder.append(user.getAsMention());
                } else {
                    builder.append(s);
                }
            }
            builder.append(" ");
        }
        return builder.toString().trim();
    }

    public static void sendStartMessage() {
        try {
            jda.awaitReady();
            jda.getTextChannelById(settings.getChannelId()).sendMessage(settings.getStartFormat()).queue();
        } catch (Exception e) {

        }
    }

    public static void sendStopMessage() {
        try {
            jda.awaitReady();
            jda.getTextChannelById(settings.getChannelId()).sendMessage(settings.getStopFormat()).queue();
        } catch (Exception e) {

        }
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

    public static Settings getSettings() {
        return settings;
    }

    public static HashMap<String, String> buildDiscordMentionables() {
        Guild g = jda.getTextChannelById(SettingsManager.getInstance().getSettings().getChannelId()).getGuild();
        g.retrieveMembers().join(); // ..you MUST NOT use join()... https://i.kym-cdn.com/entries/icons/mobile/000/024/196/sign.jpg
        HashMap<String, String> mentionables = new HashMap<>();
        for (net.dv8tion.jda.api.entities.Member member : g.getMembers()) {
            User u = member.getUser();
            mentionables.put(u.getName().toLowerCase(), u.getId());
        }
        return mentionables;
    }

}