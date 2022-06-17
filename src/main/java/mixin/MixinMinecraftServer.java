package mixin;

import main.Interweave;
import net.minecraft.network.message.MessageSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Inject(at = @At("RETURN"), method = "sendMessage")
    public void handleMessage(Text text, CallbackInfo ci) {
        if (text.getContent() instanceof TranslatableTextContent)
            Interweave.sendMessage((TranslatableTextContent) text.getContent());
    }

    @Inject(at = @At("RETURN"), method = "logChatMessage")
    public void handleChatMessage(MessageSender ms, Text text, CallbackInfo ci) {
        if (text.getContent() instanceof LiteralTextContent)
            Interweave.sendMessage(new TranslatableTextContent("chat.type.text",new Object[]{ms.name(),text.getContent()}));
    }
}