package dev.tocraft.skinshifter.neoforge;

import dev.tocraft.skinshifter.SkinShifter;
import dev.tocraft.skinshifter.SkinShifterClient;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@SuppressWarnings("unused")
@Mod(SkinShifter.MODID)
public class SkinShifterNeoForge {
    public SkinShifterNeoForge() {
        new SkinShifter().initialize();

        if (FMLEnvironment.dist.isClient())
            new SkinShifterClient().initialize();
    }
}
