package dev.tocraft.skinshifter;

import dev.tocraft.skinshifter.data.SkinPlayerData;
import net.minecraft.resources.ResourceLocation;
import tocraft.craftedcore.config.ConfigLoader;
import tocraft.craftedcore.event.common.CommandEvents;
import tocraft.craftedcore.patched.Identifier;
import tocraft.craftedcore.patched.TComponent;
import tocraft.craftedcore.platform.VersionChecker;

public class SkinShifter {
    public static final String MODID = "skinshifter";
    public static final SkinShifterConfig CONFIG = ConfigLoader.read(MODID, SkinShifterConfig.class);

    public void initialize() {
        SkinPlayerData.initialize();

        VersionChecker.registerModrinthChecker(MODID, "skinshifter", TComponent.literal("SkinShifter"));

        CommandEvents.REGISTRATION.register(new SkinShifterCommand());
    }

    public static ResourceLocation id(String name) {
        return Identifier.parse(MODID, name);
    }
}
