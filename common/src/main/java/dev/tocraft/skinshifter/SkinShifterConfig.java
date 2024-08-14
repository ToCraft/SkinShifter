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

    @Override
    public String getName() {
        return SkinShifter.MODID;
    }
}
