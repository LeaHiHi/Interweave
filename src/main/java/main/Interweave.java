package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.security.auth.login.LoginException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import utils.DiscordListener;
import utils.Settings;
import utils.SettingsManager;

public class Interweave implements DedicatedServerModInitializer {

	public static final Logger LOGGER = LogManager.getLogger();
	private static Settings settings;

	private static JDA jda;

	private static final String MOD_NAME = "Interweave";

	private static HashMap<String, String> mentionables;

	private static final ExecutorService ES = Executors
			.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Interweave").build());

	@Override
	public void onInitializeServer() {
		LOGGER.info("Interweave is Initializing!");
		
		// JDA
		ES.execute(() -> {
			try {
				settings = SettingsManager.getInstance().getSettings();
				initJDA(settings);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		// Setup server startup callback
		ServerLifecycleEvents.SERVER_STARTED.register((MinecraftServer server) -> {
	        sendStartMessage();
	        setPlayers(0, server.getMaxPlayerCount());
		});

		// Setup server end callback
		ServerLifecycleEvents.SERVER_STOPPING.register((MinecraftServer server) -> {
			LOGGER.info("Interweave shutting down.");
			sendStopMessageAndShutdown();
			ES.shutdown();
		});
		
		// Setup player connect event
		ServerLoginConnectionEvents.INIT.register((ServerLoginNetworkHandler handler, MinecraftServer server) -> {
			setPlayers(server.getCurrentPlayerCount() + 1, server.getMaxPlayerCount());
		});
		
		// Setup player disconnect event
		ServerLoginConnectionEvents.DISCONNECT.register((ServerLoginNetworkHandler handler, MinecraftServer server) -> {
			setPlayers(server.getCurrentPlayerCount() - 1, server.getMaxPlayerCount());
		});

	}

	private void initJDA(Settings settings) throws LoginException, InterruptedException {
		if (settings.getDiscordToken() == null || settings.getDiscordToken().equals("Bot token here...")) {
			log(Level.ERROR,
					"The Discord token is not configured correctly! The bot will not run. Please check your InterweaveConfig.json file.");
			return;
		}
		if (settings.getChannelId() == null || settings.getChannelId().equals("Channel Id here...")) {
			log(Level.ERROR,
					"The Channel Id is not configured correctly! The bot will not run. Please check your InterweaveConfig.json file.");
			return;
		}

		jda = JDABuilder.createLight(settings.getDiscordToken()).enableIntents(GatewayIntent.GUILD_MEMBERS).build();

		jda.addEventListener(new DiscordListener());

		jda.awaitReady();
		mentionables = buildDiscordMentionables();
	}

	public static void sendMessage(Text msg) {
		ES.execute(() -> {
			// %message% message
			// %sender% sender
			if (!(msg instanceof TranslatableText)) {
				return;
			}
			String key = ((TranslatableText) msg).getKey();
			StringBuilder messageBuilder = new StringBuilder();
			msg.visitSelf((section) -> {
				messageBuilder.append(section);
				return Optional.empty();
			});
			String message = messageBuilder.toString();
			// admin command
			if (key.equals("chat.type.admin")) {
				return;
			}
			// server control
			if (key.contains("commands")) {
				return;
			}
			// advancement
			if (key.contains("chat.type.advancement")) {
				if (settings.getAdvancementFormat() == null) {
					return;
				}
				message = settings.getAnnouncementFormat().replace("%message%", message).replace("[", "")
						.replace("]", "").replace("(", "").replace(")", "");
			}
			// Player Joined
			if (key.equals("multiplayer.player.joined")) {
				if (settings.getJoinFormat() == null) {
					return;
				}
				message = settings.getJoinFormat().replace("%message%", message);
			}
			// Player Left
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
			// Death
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
				message = settings.getEmoteFormat()
						.replace("%sender%", ((LiteralText) ((TranslatableText) msg).getArgs()[0]).getString())
						.replace("%message%", ((TranslatableText) msg).getArgs()[1].toString());
			}
			// Regular Chat
			if (key.equals("chat.type.text")) {
				if (settings.getChatFormat() == null) {
					return;
				}
				message = settings.getChatFormat()
						.replace("%sender%", ((LiteralText) ((TranslatableText) msg).getArgs()[0]).getString())
						.replace("%message%", ((TranslatableText) msg).getArgs()[1].toString());
			}
			message = findMention(message);
			try {
				jda.awaitReady();
				jda.getTextChannelById(settings.getChannelId()).sendMessage(message).queue();
			} catch (Exception e) {
				log(Level.ERROR, "Exception thrown while sending a message to Discord!", e);
			}
		});
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
		ES.execute(() -> {
			try {
				jda.awaitReady();
				jda.getTextChannelById(settings.getChannelId()).sendMessage(settings.getStartFormat()).queue();
			} catch (Exception e) {
				log(Level.ERROR, "Could not send start message to Discord!", e);
			}
		});
	}

	public static void sendStopMessageAndShutdown() {
		ES.execute(() -> {
			try {
				jda.awaitReady();
				jda.getTextChannelById(settings.getChannelId()).sendMessage(settings.getStopFormat()).queue();
				jda.shutdown();
			} catch (Exception e) {
				log(Level.ERROR, "Could not send stop message to Discord!", e);
			}
		});
	}

	public static void log(Level level, String message) {
		LOGGER.log(level, message);
	}

	public static void log(Level level, String message, Throwable t) {
		LOGGER.log(level, message, t);
	}

	public static Settings getSettings() {
		return settings;
	}

	private static HashMap<String, String> buildDiscordMentionables() {
		Guild g = jda.getTextChannelById(SettingsManager.getInstance().getSettings().getChannelId()).getGuild();
		g.retrieveMembers().join(); // ..you MUST NOT use join()...
									// https://i.kym-cdn.com/entries/icons/mobile/000/024/196/sign.jpg
		HashMap<String, String> mentionables = new HashMap<>();
		for (net.dv8tion.jda.api.entities.Member member : g.getMembers()) {
			User u = member.getUser();
			mentionables.put(u.getName().toLowerCase(), u.getId());
		}
		return mentionables;
	}

	public static void setPlayers(int current, int max) {
		ES.execute(() -> {
			try {
				jda.getPresence().setActivity(Activity.of(Activity.ActivityType.DEFAULT, current + "/" + max));
			} catch (Exception e) {
				log(Level.ERROR, "Could not set player count!", e);
			}
		});

	}
}