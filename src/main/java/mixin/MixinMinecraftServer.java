package mixin;

import main.Interweave;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @Shadow public abstract int getMaxPlayerCount();

    @Inject(at = @At("RETURN"), method = "sendSystemMessage")
    public void sendMessage(Text text, UUID uUID, CallbackInfo ci) {
        Interweave.sendMessage(text);
    }

    @Inject(at = @At("RETURN"), method = "prepareStartRegion")
    public void startServer(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        Interweave.sendStartMessage();
        Interweave.setPlayers(0, getMaxPlayerCount());
    }

    @Inject(at = @At("HEAD"), method = "shutdown")
    public void stopServer(CallbackInfo ci) {
        Interweave.sendStopMessageAndShutdown();
    }
}