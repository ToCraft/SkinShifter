package dev.tocraft.skinshifter;

import dev.tocraft.skinshifter.data.SkinPlayerData;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import tocraft.craftedcore.config.ConfigLoader;
import tocraft.craftedcore.event.common.CommandEvents;
import tocraft.craftedcore.patched.Identifier;
import tocraft.craftedcore.patched.TComponent;
import tocraft.craftedcore.platform.VersionChecker;
import tocraft.craftedcore.registration.PlayerDataRegistry;

import java.util.UUID;

import static dev.tocraft.skinshifter.data.SkinPlayerData.TAG_NAME;

@SuppressWarnings("unused")
public class SkinShifter {
    public static final String MODID = "skinshifter";
    public static final SkinShifterConfig CONFIG = ConfigLoader.read(MODID, SkinShifterConfig.class);

    public void initialize() {
        SkinPlayerData.initialize();

        VersionChecker.registerModrinthChecker(MODID, "skinshifter", TComponent.literal("SkinShifter"));

        CommandEvents.REGISTRATION.register(new SkinShifterCommand());
    }

    public static void setSkin(ServerPlayer player, UUID skinPlayer) {
        SkinPlayerData.setSkin(player, skinPlayer);
    }

    public static UUID getCurrentSkin(Player player) {
        Tag currentSkinTag = PlayerDataRegistry.readTag(player, TAG_NAME);
        if (currentSkinTag != null) {
            try {
                return UUID.fromString(currentSkinTag.getAsString());
            } catch (IllegalArgumentException ignored) {

            }
        }
        // fallback
        return player.getUUID();
    }

    public static ResourceLocation id(String name) {
        return Identifier.parse(MODID, name);
    }
}
