package dev.tocraft.skinshifter.neoforge;

import dev.tocraft.skinshifter.SkinShifter;
import net.neoforged.fml.common.Mod;

@SuppressWarnings("unused")
@Mod(SkinShifter.MODID)
public class SkinShifterNeoForge {
    public SkinShifterNeoForge() {
        new SkinShifter().initialize();
    }
}
