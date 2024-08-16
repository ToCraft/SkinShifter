package dev.tocraft.skinshifter.data;

import dev.tocraft.skinshifter.SkinShifter;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.platform.PlayerProfile;
import tocraft.craftedcore.registration.PlayerDataRegistry;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public class SkinPlayerData {
    public static final String TAG_NAME = "CurrentSkin";

    public static void initialize() {
        PlayerDataRegistry.registerKey(TAG_NAME, true, true);
    }

    @Nullable
    public static PlayerProfile getSkin(Player player) {
        UUID uuid = SkinShifter.getCurrentSkin(player);
        if (uuid != player.getUUID()) {
            PlayerProfile playerProfile = PlayerProfile.getCachedProfile(uuid);
            if (playerProfile == null) {
                // cache profile asynchronous
                CompletableFuture.runAsync(() -> PlayerProfile.ofId(uuid));
            }
            return playerProfile;
        }

        return null;
    }

    public static void setSkin(ServerPlayer player, @Nullable UUID orgSkinUUID) {
        PlayerDataRegistry.writeTag(player, TAG_NAME, orgSkinUUID != null ? StringTag.valueOf(orgSkinUUID.toString()) : StringTag.valueOf(""));
    }
}
