package dev.tocraft.skinshifter.data;

import dev.tocraft.skinshifter.SkinShifter;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.platform.PlayerProfile;
import tocraft.craftedcore.registration.PlayerDataRegistry;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public class SkinPlayerData {
    public static final String TAG_NAME = "CurrentSkin";
    private static final Map<UUID, ShiftPlayerSkin> CACHED_SKINS = new ConcurrentHashMap<>();

    public static void initialize() {
        PlayerDataRegistry.registerKey(TAG_NAME, true, true);
    }

    @Nullable
    public static ShiftPlayerSkin getSkin(Player player) {
        UUID uuid = SkinShifter.getCurrentSkin(player);
        if (uuid != player.getUUID()) {
                if (!CACHED_SKINS.containsKey(uuid)) {
                    // do this in an external thread so the game isn't stuck with bad internet connection
                    new Thread(() -> {
                        PlayerProfile playerProfile = PlayerProfile.ofId(uuid);
                        ShiftPlayerSkin skin = ShiftPlayerSkin.byPlayerProfile(playerProfile);
                        CACHED_SKINS.put(uuid, skin);
                    }).start();
                }

                return CACHED_SKINS.get(uuid);
            }

        return null;
    }

    public static void setSkin(ServerPlayer player, @Nullable UUID orgSkinUUID) {
        PlayerDataRegistry.writeTag(player, TAG_NAME, orgSkinUUID != null ? StringTag.valueOf(orgSkinUUID.toString()) : StringTag.valueOf(""));
    }
}
