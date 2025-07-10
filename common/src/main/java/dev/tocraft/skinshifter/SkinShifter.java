package dev.tocraft.skinshifter;

import dev.tocraft.craftedcore.config.ConfigLoader;
import dev.tocraft.craftedcore.event.common.CommandEvents;
import dev.tocraft.craftedcore.platform.VersionChecker;
import dev.tocraft.craftedcore.registration.PlayerDataRegistry;
import dev.tocraft.skinshifter.data.SkinPlayerData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

import static dev.tocraft.skinshifter.data.SkinPlayerData.SKIN_TAG_NAME;

@SuppressWarnings("unused")
public class SkinShifter {
    public static final String MODID = "skinshifter";
    public static final SkinShifterConfig CONFIG = ConfigLoader.register(MODID);

    public void initialize() {
        SkinPlayerData.initialize();

        VersionChecker.registerModrinthChecker(MODID, "skinshifter", Component.literal("SkinShifter"));

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
     * @param player the player the skin should be set of
     * @param uri    the uri / url of the new skin
     * @param slim   whether the skin is slim or wide
     */
    public static void setSkinURI(@NotNull ServerPlayer player, String uri, boolean slim) {
        SkinPlayerData.setSkinURI(player, uri, slim);
    }

    /**
     * @param player the player that is being tested
     * @return the uuid of the owner of the current skin the player wears
     */
    public static UUID getCurrentSkin(Player player) {
        String currentSkin = (String) PlayerDataRegistry.readTag(player, SKIN_TAG_NAME);
        if (currentSkin != null) {
            try {
                return UUID.fromString(currentSkin);
            } catch (IllegalArgumentException ignored) {

            }
        }
        // fallback
        return player.getUUID();
    }
}
