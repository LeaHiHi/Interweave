package mixin;

import main.Interweave;
import net.minecraft.network.message.MessageType;
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
    public void handleChatMessage(Text message, MessageType.Parameters params, String prefix, CallbackInfo ci) {
        if (message.getContent() instanceof LiteralTextContent)
            Interweave.sendMessage(new TranslatableTextContent("chat.type.text",new Object[]{params,message.getContent(),prefix}));
    }
}