package dev.tocraft.skinshifter;

import tocraft.craftedcore.config.Config;
import tocraft.craftedcore.config.annotions.Synchronize;

@SuppressWarnings("CanBeFinal")
public class SkinShifterConfig implements Config {
    @Synchronize
    public boolean changeCape = true;
    @Synchronize
    public boolean changeNameTag = true;
    @Synchronize
    public boolean changeChatName = true;
    public int baseCommandOPLevel = 2;
    public int selfCommandOPLevel = 0;

    @Override
    public String getName() {
        return SkinShifter.MODID;
    }
}
