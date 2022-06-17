package utils;

import main.Interweave;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.*;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

public class DiscordListener extends ListenerAdapter {
    private PlayerManager pm;

    public DiscordListener() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> pm = server.getPlayerManager());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (pm == null) {
            return;
        }
        JDA jda = event.getJDA();

        User author = event.getAuthor();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        if (!channel.getId().equals(Interweave.getSettings().getChannelId())) {
            return;
        }
        if (author.getId().equals(jda.getSelfUser().getId())) {
            return;
        }
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }
        Interweave.log(Level.INFO, "<" + event.getAuthor().getAsTag() + "> " + event.getMessage().getContentDisplay());
        if (SettingsManager.getInstance().getSettings().getDingPlayersIfNameFound() != null && SettingsManager.getInstance().getSettings().getDingPlayersIfNameFound()) { // :(
            pm.getPlayerList().stream().findAny()
                    .filter(spe -> event.getMessage().getContentDisplay().contains(spe.getName().getContent().toString()))
                    .ifPresent(PlayerUtils::dingPlayer);
        }
        try {
            String authorHover = event.getGuild().getName()
                    + " §o#" + event.getChannel().getName()
                    + "§r\n" + author.getName() + '#'
                    + author.getDiscriminator() + ' '
                    + (author.isBot() ? "§3Bot§r" : "§2User§r")
                    + '\n' + author.getId();
            String messageText = Interweave.getSettings().getDiscordToMinecraftFormat().replace("%sender%", author.getName()).replace("%message%", message.getContentDisplay());
            if (message.getAttachments().size() > 0) {
                messageText = messageText + " " + buildAttachmentsUrl(message.getAttachments()); // append attachments URLs to the end of the message
            }
            if (message.getEmbeds().size() > 0) {
                messageText = messageText + '\n' + buildEmbeds(message.getEmbeds());
            }
            pm.broadcast(Text.of(messageText),MessageType.SYSTEM);
            Interweave.setLastMessage(messageText);
        } catch (Exception ex) {
            Interweave.log(Level.ERROR, "Could not send Discord message to Minecraft!", ex);
        }
    }

    private String buildAttachmentsUrl(List<Message.Attachment> attachments) {
        StringJoiner joiner = new StringJoiner(" ");
        for (Message.Attachment attachment : attachments) {
            joiner.add(attachment.getUrl());
        }
        return joiner.toString();
    }

    private String buildEmbeds(List<MessageEmbed> embeds) {
        StringJoiner joiner = new StringJoiner("\n");
        for (MessageEmbed e : embeds) {
            joiner.add(EmbedFormatter.formatEmbed(e));
        }
        return joiner.toString();
    }
}
