package dev.tocraft.skinshifter.forge;

import dev.tocraft.skinshifter.SkinShifter;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod(SkinShifter.MODID)
public class SkinShifterForge {
    public SkinShifterForge() {
        new SkinShifter().initialize();
    }
}
