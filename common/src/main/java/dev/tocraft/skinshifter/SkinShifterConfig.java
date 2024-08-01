package dev.tocraft.skinshifter;

import tocraft.craftedcore.config.Config;

@SuppressWarnings("CanBeFinal")
public class SkinShifterConfig implements Config {
    public boolean changeCape = true;

    @Override
    public String getName() {
        return SkinShifter.MODID;
    }
}
