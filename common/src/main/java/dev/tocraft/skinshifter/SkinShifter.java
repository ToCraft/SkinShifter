package dev.tocraft.skinshifter;

import net.minecraft.resources.ResourceLocation;
import tocraft.craftedcore.patched.Identifier;

public class SkinShifter {
    public static final String MODID = "skinshifter";

    public void initialize() {

    }

    public static ResourceLocation id(String name) {
        return Identifier.parse(MODID, name);
    }
}
