package dev.tocraft.skinshifter;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.tocraft.skinshifter.data.SkinPlayerData;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import tocraft.craftedcore.event.common.CommandEvents;
import tocraft.craftedcore.patched.CCommandSourceStack;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SkinShifterCommand implements CommandEvents.CommandRegistration {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection selection) {
        onRegister(dispatcher);
    }

    private void onRegister(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> rootNode = Commands.literal(SkinShifter.MODID).requires(source -> source.hasPermission(SkinShifter.CONFIG.baseCommandOPLevel) || source.hasPermission(SkinShifter.CONFIG.selfCommandOPLevel)).build();
        LiteralCommandNode<CommandSourceStack> set = Commands.literal("set")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("playerUUID", UuidArgument.uuid())
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    ServerPlayer sender;
                                    try {
                                        sender = context.getSource().getPlayerOrException();

                                    } catch (CommandSyntaxException e) {
                                        sender = null;
                                    }
                                    if (sender != null && sender.getUUID() == player.getUUID()) {
                                        if (!context.getSource().hasPermission(SkinShifter.CONFIG.selfCommandOPLevel)) {
                                            throw new SimpleCommandExceptionType(Component.translatable("craftedcore.command.invalid_perms")).create();
                                        }
                                    } else if (!context.getSource().hasPermission(SkinShifter.CONFIG.baseCommandOPLevel)) {
                                        throw new SimpleCommandExceptionType(Component.translatable("craftedcore.command.invalid_perms")).create();
                                    }
                                    UUID playerUUID = UuidArgument.getUuid(context, "playerUUID");
                                    SkinShifter.setSkin(player, playerUUID);
                                    // run async in case of bad internet connection
                                    SkinPlayerData.getSkinProfile(playerUUID).thenAccept(profile -> CCommandSourceStack.sendSuccess(context.getSource(), Component.translatable("skinshifter.command.set", player.getName(), profile.orElse(player.getGameProfile()).getName()), true));
                                    return 1;
                                }))
                        .then(Commands.argument("playerName", MessageArgument.message())
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    ServerPlayer sender;
                                    try {
                                        sender = context.getSource().getPlayerOrException();

                                    } catch (CommandSyntaxException e) {
                                        sender = null;
                                    }
                                    if (sender != null && sender.getUUID() == player.getUUID()) {
                                        if (!context.getSource().hasPermission(SkinShifter.CONFIG.selfCommandOPLevel)) {
                                            throw new SimpleCommandExceptionType(Component.translatable("craftedcore.command.invalid_perms")).create();
                                        }
                                    } else if (!context.getSource().hasPermission(SkinShifter.CONFIG.baseCommandOPLevel)) {
                                        throw new SimpleCommandExceptionType(Component.translatable("craftedcore.command.invalid_perms")).create();
                                    }
                                    String playerName = MessageArgument.getMessage(context, "playerName").getString();
                                    // run async in case of bad internet connection
                                    @NotNull CompletableFuture<Optional<GameProfile>> profileFuture = SkinPlayerData.getSkinProfile(playerName);
                                    profileFuture.thenAccept(profile -> {
                                        if (profile.isEmpty()) {
                                            CCommandSourceStack.sendSuccess(context.getSource(), Component.translatable("skinshifter.invalid_player", playerName), true);
                                        } else {
                                            SkinShifter.setSkin(player, profile.get().getId());
                                            CCommandSourceStack.sendSuccess(context.getSource(), Component.translatable("skinshifter.command.set", player.getName(), playerName), true);
                                        }
                                    });
                                    return 1;
                                }))).build();

        LiteralCommandNode<CommandSourceStack> reset = Commands.literal("reset")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            ServerPlayer sender;
                            try {
                                sender = context.getSource().getPlayerOrException();

                            } catch (CommandSyntaxException e) {
                                sender = null;
                            }
                            if (sender != null && sender.getUUID() == player.getUUID()) {
                                if (!context.getSource().hasPermission(SkinShifter.CONFIG.selfCommandOPLevel)) {
                                    throw new SimpleCommandExceptionType(Component.translatable("craftedcore.command.invalid_perms")).create();
                                }
                            } else if (!context.getSource().hasPermission(SkinShifter.CONFIG.baseCommandOPLevel)) {
                                throw new SimpleCommandExceptionType(Component.translatable("craftedcore.command.invalid_perms")).create();
                            }
                            SkinShifter.setSkin(player, null);
                            CCommandSourceStack.sendSuccess(context.getSource(), Component.translatable("skinshifter.command.reset", player.getName()), true);
                            return 1;
                        })).build();

        LiteralCommandNode<CommandSourceStack> changeChatName = Commands.literal("changeChatName").requires(source -> source.hasPermission(SkinShifter.CONFIG.baseCommandOPLevel))
                .executes(context -> {
                    boolean bool = SkinShifter.CONFIG.changeName;
                    CCommandSourceStack.sendSuccess(context.getSource(), Component.translatable("craftedcore.config.get", "changeChatName", String.valueOf(bool)), true);
                    return 1;
                })
                .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(context -> {
                            boolean bool = BoolArgumentType.getBool(context, "value");
                            SkinShifter.CONFIG.changeName = bool;
                            SkinShifter.CONFIG.save();
                            SkinShifter.CONFIG.sendToAllPlayers(context.getSource().getLevel());
                            CCommandSourceStack.sendSuccess(context.getSource(), Component.translatable("craftedcore.config.set", "changeChatName", String.valueOf(bool)), true);
                            return 1;
                        })).build();

        rootNode.addChild(set);
        rootNode.addChild(reset);
        rootNode.addChild(changeChatName);

        dispatcher.getRoot().addChild(rootNode);
    }
}
