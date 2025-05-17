package dev.tocraft.skinshifter;

import com.mojang.logging.LogUtils;
import dev.tocraft.skinshifter.data.SkinPlayerData;
import dev.tocraft.skinshifter.data.TextureCache;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import tocraft.craftedcore.config.ConfigLoader;
import tocraft.craftedcore.event.common.CommandEvents;
import tocraft.craftedcore.patched.TComponent;
import tocraft.craftedcore.platform.VersionChecker;
import tocraft.craftedcore.registration.PlayerDataRegistry;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static dev.tocraft.skinshifter.data.SkinPlayerData.SKIN_TAG_NAME;
import static dev.tocraft.skinshifter.data.SkinPlayerData.URI_TAG_NAME;

@SuppressWarnings("unused")
public class SkinShifter {
    public static final String MODID = "skinshifter";
    public static final SkinShifterConfig CONFIG = ConfigLoader.register(MODID);

    public void initialize() {
        SkinPlayerData.initialize();

        VersionChecker.registerModrinthChecker(MODID, "skinshifter", TComponent.literal("SkinShifter"));

        CommandEvents.REGISTRATION.register(new SkinShifterCommand());
    }

    /**
     * @param player     the player the skin should be set of
     * @param skinPlayer the uuid of the owner of the new skin
     */
    public static void setSkin(@NotNull ServerPlayer player, UUID skinPlayer) {
        if (Objects.equals(player.getUUID(), skinPlayer)) {
            SkinPlayerData.setSkin(player, null);
        } else {
            SkinPlayerData.setSkin(player, skinPlayer);
        }
    }

    /**
     * @param player     the player the skin should be set of
     * @param uri        the uri / url of the new skin
     * @param slim       whether the skin is slim or wide
     */
    public static void setSkinURI(@NotNull ServerPlayer player, String uri, boolean slim) {
        SkinPlayerData.setSkinURI(player, uri, slim);
    }

    /**
     * @param player the player that is being tested
     * @return the uuid of the owner of the current skin the player wears
     */
    public static UUID getCurrentSkin(Player player) {
        Tag currentSkinTag = PlayerDataRegistry.readTag(player, SKIN_TAG_NAME);
        if (currentSkinTag != null) {
            try {
                //#if MC>=1215
                return currentSkinTag.asString().map(UUID::fromString).orElse(player.getUUID());
                //#else
                //$$ return UUID.fromString(currentSkinTag.getAsString());
                //#endif
            } catch (IllegalArgumentException ignored) {

            }
        }
        // fallback
        return player.getUUID();
    }

    /**
     * @param player the player that is being tested
     * @return a future containing an optional {@link PlayerSkin} object
     */
    @SuppressWarnings("deprecation")
    public static @NotNull CompletableFuture<Optional<PlayerSkin>> getSkinObj(Player player) {
        return SkinPlayerData.getPlayerSkin(player).thenApply(skin -> {
            if (skin.isEmpty()) {
                Optional<CompoundTag> currentUriTag = Optional.ofNullable((CompoundTag) PlayerDataRegistry.readTag(player, URI_TAG_NAME));
                if (currentUriTag.isPresent()) {
                    //#if MC>=1215
                    String uri = currentUriTag.get().getString("uri").orElse(null);
                    boolean slim = currentUriTag.get().getBooleanOr("slim", false);
                    //#else
                    //$$ String uri = currentUriTag.get().getString("uri");
                    //$$ boolean slim = currentUriTag.get().getBoolean("slim");
                    //#endif

                    if (uri != null) {
                        try {
                            URL url = URI.create(uri).toURL();
                            Optional<ResourceLocation> id = TextureCache.getSkinTextureId(url);

                            if (id.isPresent()) {
                                return Optional.of(new PlayerSkin(id.get(), uri, null, null, slim ? PlayerSkin.Model.SLIM : PlayerSkin.Model.WIDE, true));
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
}
