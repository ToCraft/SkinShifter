package dev.tocraft.skinshifter;

import dev.tocraft.craftedcore.config.Config;
import dev.tocraft.craftedcore.config.annotions.Synchronize;

@SuppressWarnings("CanBeFinal")
public class SkinShifterConfig implements Config {
    @Synchronize
    public boolean changeName = true;
    public int baseCommandOPLevel = 2;
    public int selfCommandOPLevel = 0;

    @Override
    public String getName() {
        return SkinShifter.MODID;
    }
}
