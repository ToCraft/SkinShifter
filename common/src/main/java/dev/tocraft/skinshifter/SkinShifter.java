package dev.tocraft.skinshifter;

import dev.tocraft.skinshifter.data.SkinPlayerData;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import tocraft.craftedcore.config.ConfigLoader;
import tocraft.craftedcore.event.common.CommandEvents;
import tocraft.craftedcore.patched.TComponent;
import tocraft.craftedcore.platform.VersionChecker;
import tocraft.craftedcore.registration.PlayerDataRegistry;

import java.util.Objects;
import java.util.UUID;

import static dev.tocraft.skinshifter.data.SkinPlayerData.TAG_NAME;

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
     * @param player the player that is being tested
     * @return the uuid of the owner of the current skin the player wears
     */
    public static UUID getCurrentSkin(Player player) {
        Tag currentSkinTag = PlayerDataRegistry.readTag(player, TAG_NAME);
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
}
