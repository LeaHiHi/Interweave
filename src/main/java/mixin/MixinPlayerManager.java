package mixin;

import main.Interweave;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
    @Shadow @Final private List<ServerPlayerEntity> players;

    @Shadow @Final protected int maxPlayers;

    @Inject(at = @At("RETURN"), method = "onPlayerConnect")
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData ccd, CallbackInfo ci) {
        Interweave.setPlayers(players.size(), maxPlayers);
    }

    @Inject(at = @At("RETURN"), method = "remove")
    public void remove(ServerPlayerEntity player, CallbackInfo ci) {
        Interweave.setPlayers(players.size(), maxPlayers);
    }

//    @Inject(at = @At("RETURN"), method = "broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/util/registry/RegistryKey;)V")
//    public void broadcast(SignedMessage text, Function f, MessageSender s, RegistryKey rk, CallbackInfo ci) {
////        if (!text.getContent().equals(Text.of(Interweave.getLastMessage())))
////            Interweave.sendMessage(text.getContent());
//        System.out.println("asdhkjfjklahdsfjklbadghjbklagdhbjkladsghkbjasdgjklahsdgadghjklbadsgjkhladsgadgkjagdshljk");
//    }
}
