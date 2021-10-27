package mixin;

import main.Interweave;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
    @Inject(at = @At("RETURN"), method = "sendSystemMessage")
    public void copySystemMessageToDiscord(Text text, UUID uUID, CallbackInfo ci) {
        Interweave.sendMessage(text);
    }
}