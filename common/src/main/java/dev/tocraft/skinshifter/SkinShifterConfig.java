package dev.tocraft.skinshifter;

import dev.tocraft.craftedcore.config.Config;
import dev.tocraft.craftedcore.config.annotions.Comment;
import dev.tocraft.craftedcore.config.annotions.Synchronize;

@SuppressWarnings("CanBeFinal")
public class SkinShifterConfig implements Config {
    @Comment("Enable permission system. When true, permissions override server config. When false, uses original server config behavior.")
    public boolean usePermissions = false;
    @Comment("Set 'usePermissions' to 'false' to use this option!")
    public int baseCommandOPLevel = 2;
    @Comment("Set 'usePermissions' to 'false' to use this option!")
    public int selfCommandOPLevel = 0;
    @Synchronize
    public boolean changeName = true;

    @Override
    public String getName() {
        return SkinShifter.MODID;
    }
}
