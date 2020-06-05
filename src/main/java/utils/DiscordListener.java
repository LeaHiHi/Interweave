package utils;

import main.Interweave;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class DiscordListener extends ListenerAdapter {
    private PlayerManager pm;

    public DiscordListener() {
        ServerStartCallback.EVENT.register(server -> pm = server.getPlayerManager());
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
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
        try {
            Style style = Style.EMPTY;
            String messageText = Interweave.getSettings().getDiscordToMinecraftFormat().replace("%sender%", author.getName()).replace("%message%", message.getContentDisplay());
            if (message.getAttachments().size() > 0) {
                messageText = messageText + " " + buildAttachmentsUrl(message.getAttachments()); // append attachments URLs to the end of the message
            }
            if (pm != null) {
                pm.sendToAll(new GameMessageS2CPacket(new LiteralText(messageText).setStyle(style), MessageType.CHAT, UUID.randomUUID()));
            }
        } catch (Exception ex) {
            Interweave.log(Level.ERROR, "Could not send Discord message to Minecraft!", ex);
        }
    }

    private String buildAttachmentsUrl(List<Message.Attachment> a) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; ; i++) {
            sb.append(a.get(i - 1).getUrl());
            if (i == a.size()) {
                return sb.toString();
            }
            sb.append(' ');
        }
    }
}
