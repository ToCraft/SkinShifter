package dev.tocraft.skinshifter.data;

import com.mojang.authlib.GameProfile;
import dev.tocraft.skinshifter.SkinShifter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.platform.PlayerProfile;
import tocraft.craftedcore.registration.PlayerDataRegistry;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public class SkinPlayerData {
    public static final String SKIN_TAG_NAME = "CurrentSkin";
    public static final String URI_TAG_NAME = "CurrentSkinURI";

    public static void initialize() {
        PlayerDataRegistry.registerKey(SKIN_TAG_NAME, true, true);
        PlayerDataRegistry.registerKey(URI_TAG_NAME, true, true);
    }

    @Nullable
    @Deprecated(forRemoval = true)
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

    public static @NotNull CompletableFuture<Optional<GameProfile>> getSkinProfile(Player player) {
        UUID uuid = SkinShifter.getCurrentSkin(player);
        if (uuid != player.getUUID()) {
            return getSkinProfile(uuid);
        } else {
            return CompletableFuture.completedFuture(Optional.empty());
        }
    }

    public static @NotNull CompletableFuture<Optional<GameProfile>> getSkinProfile(UUID uuid) {
        return SkullBlockEntity.fetchGameProfile(uuid);
    }

    public static @NotNull CompletableFuture<Optional<GameProfile>> getSkinProfile(String name) {
        return SkullBlockEntity.fetchGameProfile(name);
    }

    /**
     * @deprecated Use {@link SkinShifter#getSkinObj(Player)} instead
     */
    @Deprecated
    @Environment(EnvType.CLIENT)
    public static @NotNull CompletableFuture<Optional<PlayerSkin>> getPlayerSkin(Player player) {
        CompletableFuture<Optional<GameProfile>> profileFuture = getSkinProfile(player);
        return profileFuture.thenApply(profile -> profile.map(gameProfile -> Minecraft.getInstance().getSkinManager().getInsecureSkin(gameProfile)));
    }

    public static void setSkin(ServerPlayer player, @Nullable UUID orgSkinUUID) {
        PlayerDataRegistry.writeTag(player, SKIN_TAG_NAME, orgSkinUUID != null ? StringTag.valueOf(orgSkinUUID.toString()) : StringTag.valueOf(""));
    }

    public static void setSkinURI(ServerPlayer player, @Nullable String uri, boolean slim) {
        if (uri != null) {
            CompoundTag tag = new CompoundTag();
            tag.putString("uri", uri);
            tag.putBoolean("slim", slim);
            PlayerDataRegistry.writeTag(player, URI_TAG_NAME, tag);
        } else {
            PlayerDataRegistry.writeTag(player, URI_TAG_NAME, new CompoundTag());
        }
    }
}
