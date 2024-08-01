package dev.tocraft.skinshifter.forge;

import dev.tocraft.skinshifter.SkinShifter;
import dev.tocraft.skinshifter.SkinShifterClient;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@SuppressWarnings("unused")
@Mod(SkinShifter.MODID)
public class SkinShifterForge {
    public SkinShifterForge() {
        new SkinShifter().initialize();

        if (FMLEnvironment.dist.isClient()) {
            new SkinShifterClient();
        }
    }
}
