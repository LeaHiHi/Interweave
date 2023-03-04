package utils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class PlayerUtils {
    /**
     * Dings a player. You got mail!
     *
     * @param serverPlayerEntity The ServerPlayerEntity to ding.
     */
    public static void dingPlayer(ServerPlayerEntity serverPlayerEntity) {
        serverPlayerEntity.playSound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.MASTER, 1.0F, 1.0F);
    }
}
