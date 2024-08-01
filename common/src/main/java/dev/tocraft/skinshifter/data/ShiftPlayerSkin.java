package dev.tocraft.skinshifter.data;

import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.platform.PlayerProfile;

import java.net.URL;

public record ShiftPlayerSkin(@Nullable URL skin, boolean isSlim, @Nullable URL cape) {
    public static ShiftPlayerSkin byPlayerProfile(@Nullable PlayerProfile playerProfile) {
        if (playerProfile != null) {
            return new ShiftPlayerSkin(playerProfile.skin(), playerProfile.isSlim(), playerProfile.cape());
        }
        else {
            return null;
        }
    }
}
