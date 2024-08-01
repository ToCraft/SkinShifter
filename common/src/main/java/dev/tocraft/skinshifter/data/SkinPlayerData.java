package dev.tocraft.skinshifter.data;

import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.platform.PlayerProfile;
import tocraft.craftedcore.registration.PlayerDataRegistry;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SkinPlayerData {
    private static final String TAG_NAME = "CurrentSkin";
    private static final Map<UUID, ShiftPlayerSkin> CACHED_SKINS = new ConcurrentHashMap<>();

    public static void initialize() {
        PlayerDataRegistry.registerKey(TAG_NAME, true, true);
    }

    @Nullable
    public static ShiftPlayerSkin getSkin(Player player) {
        Tag currentSkinTag = PlayerDataRegistry.readTag(player, TAG_NAME);
        if (currentSkinTag != null) {
            String currentSkinId = currentSkinTag.getAsString();
            if (!currentSkinId.isBlank()) {
                UUID uuid = UUID.fromString(currentSkinId);
                if (!CACHED_SKINS.containsKey(uuid)) {
                    // do this in an external thread so the game isn't stuck with bad internet connection
                    new Thread(() -> {
                        PlayerProfile playerProfile = PlayerProfile.ofId(UUID.fromString(currentSkinId));
                        ShiftPlayerSkin skin = ShiftPlayerSkin.byPlayerProfile(playerProfile);
                        CACHED_SKINS.put(uuid, skin);
                    }).start();
                }

                return CACHED_SKINS.get(uuid);
            }
        }

        return null;
    }

    public static void setSkin(Player player, @Nullable UUID orgSkinUUID) {
        PlayerDataRegistry.writeTag(player, TAG_NAME, orgSkinUUID != null ? StringTag.valueOf(orgSkinUUID.toString()) : null);
    }
}
