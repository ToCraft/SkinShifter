package dev.tocraft.skinshifter.data;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.patched.TComponent;
import tocraft.craftedcore.platform.PlayerProfile;

import java.net.URL;
import java.util.UUID;

@ApiStatus.Internal
public record ShiftPlayerSkin(@NotNull UUID owner, @Nullable Component displayName, @Nullable URL skin, boolean isSlim, @Nullable URL cape) {
    public static ShiftPlayerSkin byPlayerProfile(@Nullable PlayerProfile playerProfile) {
        if (playerProfile != null) {
            return new ShiftPlayerSkin(playerProfile.id(), TComponent.literal(playerProfile.name()), playerProfile.skin(), playerProfile.isSlim(), playerProfile.cape());
        }
        else {
            return null;
        }
    }
}
