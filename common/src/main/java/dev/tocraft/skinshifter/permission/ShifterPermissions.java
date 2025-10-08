package dev.tocraft.skinshifter.permission;

import dev.tocraft.craftedcore.permission.PermissionChecker;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Cross-platform permission manager interface for skinshifter mod.
 * Implementations handle platform-specific permission checks.
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class ShifterPermissions {

    /**
     * Check if a player has a specific permission node
     *
     * @param player     The player to check
     * @param permission The permission node to check
     * @return true if the player has the permission
     */
    public static boolean hasPermission(@NotNull ServerPlayer player, @NotNull String permission) {
        return PermissionChecker.hasPermission(player, "skinshifter", permission);
    }


    /**
     * Check if a player has permission to use a specific command
     *
     * @param player  The player to check
     * @param command The command name (e.g., "addShape", "removeShape")
     * @return true if the player can use the command
     */
    public static boolean canUseCommand(@NotNull ServerPlayer player, @NotNull String command) {
        return hasPermission(player, "command." + command);
    }

    /**
     * Check if a player has permission to use a command on themselves
     *
     * @param player  The player to check
     * @param command The command name (e.g., "addShape", "removeShape")
     * @return true if the player can use the command on themselves
     */
    public static boolean canUseCommandOnSelf(@NotNull ServerPlayer player, @NotNull String command) {
        return hasPermission(player, "command." + command + ".self") ||
                canUseCommand(player, command);
    }

    /**
     * Check if a player has permission to use a command on others
     *
     * @param player  The player to check
     * @param command The command name (e.g., "addShape", "removeShape")
     * @return true if the player can use the command on others
     */
    public static boolean canUseCommandOnOthers(@NotNull ServerPlayer player, @NotNull String command) {
        return hasPermission(player, "command." + command + ".others") ||
                canUseCommand(player, command);
    }

    /**
     * Check if a player has permission to use a command on a specific target
     *
     * @param executor The player executing the command
     * @param target   The target player
     * @param command  The command name (e.g., "addShape", "removeShape")
     * @return true if the player can use the command on the target
     */
    public static boolean canUseCommandOnTarget(@NotNull ServerPlayer executor, @NotNull ServerPlayer target, @NotNull String command) {
        if (executor.getUUID().equals(target.getUUID())) {
            // Using command on self
            return canUseCommandOnSelf(executor, command);
        } else {
            // Using command on others
            return canUseCommandOnOthers(executor, command);
        }
    }
}
