package dev.tocraft.skinshifter.data;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import dev.tocraft.craftedcore.registration.PlayerDataRegistry;
import dev.tocraft.skinshifter.SkinShifter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.ClientAsset;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public class SkinPlayerData {
    public static final String SKIN_TAG_NAME = "CurrentSkin";
    public static final String URI_TAG_NAME = "CurrentSkinURI";

    public static void initialize() {
        PlayerDataRegistry.registerKey(SKIN_TAG_NAME, Codec.STRING, true, true);
        PlayerDataRegistry.registerKey(URI_TAG_NAME, CompoundTag.CODEC, true, true);
    }

    public static @NotNull Optional<GameProfile> getSkinProfile(Player player) {
        UUID uuid = SkinShifter.getCurrentSkin(player);
        if (uuid != player.getUUID()) {
            return getSkinProfile(uuid);
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<GameProfile> getSkinProfile(UUID uuid) {
        return Minecraft.getInstance().services().profileResolver().fetchById(uuid);
    }

    public static @NotNull Optional<GameProfile> getSkinProfile(String name) {
        return Minecraft.getInstance().services().profileResolver().fetchByName(name);
    }

    @Environment(EnvType.CLIENT)
    public static @NotNull CompletableFuture<Optional<PlayerSkin>> getPlayerSkin(Player player) {
        CompletableFuture<Optional<GameProfile>> profileFuture = CompletableFuture.completedFuture(getSkinProfile(player));
        return profileFuture.thenApply(profile -> {
            Optional<PlayerSkin> skin = profile.map(gameProfile -> Minecraft.getInstance().getSkinManager().createLookup(gameProfile, true).get());
            if (skin.isEmpty()) {
                // test if there is a url
                Optional<CompoundTag> currentUriTag = Optional.ofNullable(PlayerDataRegistry.readTag(player, URI_TAG_NAME, CompoundTag.class));
                if (currentUriTag.isPresent()) {
                    String uri = currentUriTag.get().getStringOr("uri", "");
                    boolean slim = currentUriTag.get().getBooleanOr("slim", false);

                    if (!uri.isEmpty()) {
                        try {
                            URL url = URI.create(uri).toURL();
                            Optional<ResourceLocation> id = TextureCache.loadSkinTexture(url);

                            if (id.isPresent()) {
                                return Optional.of(new PlayerSkin(new ClientAsset.ResourceTexture(id.get()), null, null, slim ? PlayerModelType.SLIM : PlayerModelType.WIDE, true));
                            }
                        } catch (MalformedURLException e) {
                            LogUtils.getLogger().error("Invalid URI specified: {}", uri, e);
                        }
                    }
                }
            }
            return skin;
        });
    }

    public static void setSkin(ServerPlayer player, @Nullable UUID orgSkinUUID) {
        PlayerDataRegistry.writeTag(player, SKIN_TAG_NAME, orgSkinUUID != null ? orgSkinUUID.toString() : "");
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
