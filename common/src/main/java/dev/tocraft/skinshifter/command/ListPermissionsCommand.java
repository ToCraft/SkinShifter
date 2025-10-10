package dev.tocraft.skinshifter.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

/**
 * Simple command to list all skinshifter permissions for easy copy-paste into LuckPerms
 */
public class ListPermissionsCommand {

    public static LiteralCommandNode<CommandSourceStack> createNode() {
        return Commands.literal("list-permissions")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    listAllPermissions(context.getSource());
                    return 1;
                }).build();
    }

    private static void listAllPermissions(@NotNull CommandSourceStack source) {
        int size = countPermissions();

        source.sendSuccess(() -> Component.literal("§6=== SkinShifter Permissions List ==="), false);
        source.sendSuccess(() -> Component.literal("§eClick on the commands to run them:"), false);
        source.sendSuccess(() -> Component.literal(""), false);

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§a§lCommand Permissions:"), false);

        // Command Permissions
        sendClickableCommand(source, "skinshifter.command.set", "/lp group admin permission set skinshifter.command.set true");
        sendClickableCommand(source, "skinshifter.command.set.self", "/lp group admin permission set skinshifter.command.set.self true");
        sendClickableCommand(source, "skinshifter.command.set.others", "/lp group admin permission set skinshifter.command.set.others true");
        sendClickableCommand(source, "skinshifter.command.uri", "/lp group admin permission set skinshifter.command.uri true");
        sendClickableCommand(source, "skinshifter.command.uri.self", "/lp group admin permission set skinshifter.command.uri.self true");
        sendClickableCommand(source, "skinshifter.command.uri.others", "/lp group admin permission set skinshifter.command.uri.others true");
        sendClickableCommand(source, "skinshifter.command.reset", "/lp group admin permission set skinshifter.command.reset true");
        sendClickableCommand(source, "skinshifter.command.reset.self", "/lp group admin permission set skinshifter.command.reset.self true");
        sendClickableCommand(source, "skinshifter.command.reset.others", "/lp group admin permission set skinshifter.command.reset.others true");
        sendClickableCommand(source, "skinshifter.command.playerName", "/lp group admin permission set skinshifter.command.playerName true");

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§a§lWildcard Permissions:"), false);

        // Wildcard Permissions
        sendClickableCommand(source, "skinshifter.* §7# All permissions", "/lp group admin permission set skinshifter.* true");
        sendClickableCommand(source, "skinshifter.command.* §7# All commands", "/lp group admin permission set skinshifter.command.* true");

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§e§lTotal: " + size + " permissions available"), false);
        source.sendSuccess(() -> Component.literal("§7Note: These commands must be run in the **server console** or a LuckPerms web editor."), false);
    }

    /**
     * Helper method to create and send a clickable chat component.
     * The command will be executed when the component is clicked.
     * The displayed text is set to white (§f).
     */
    private static void sendClickableCommand(@NotNull CommandSourceStack source, String displayPermission, String command) {
        // Create a style object with a ClickEvent
        Style clickableStyle = Style.EMPTY.withClickEvent(new ClickEvent.RunCommand(command));

        // Create the component with the desired text and apply the style
        Component clickableComponent = Component.literal("§f" + command + (displayPermission.contains("#") ? (" " + displayPermission.substring(displayPermission.indexOf("#"))) : ""))
                .withStyle(clickableStyle);

        source.sendSuccess(() -> clickableComponent, false);
    }

    private static int countPermissions() {
        int i = 0;

        // Command permissions
        i++;   // skinshifter.command.set
        i++;   // skinshifter.command.set.self
        i++;   // skinshifter.command.set.others
        i++;   // skinshifter.command.uri
        i++;   // skinshifter.command.uri.self
        i++;   // skinshifter.command.uri.others
        i++;   // skinshifter.command.reset
        i++;   // skinshifter.command.reset.self
        i++;   // skinshifter.command.reset.others
        i++;   // skinshifter.command.playerName

        return i;
    }
}