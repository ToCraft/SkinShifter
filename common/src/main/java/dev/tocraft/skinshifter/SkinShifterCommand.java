package dev.tocraft.skinshifter;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
//#if MC>1182
import net.minecraft.commands.CommandBuildContext;
//#endif
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import tocraft.craftedcore.event.common.CommandEvents;
import tocraft.craftedcore.patched.CCommandSourceStack;
import tocraft.craftedcore.patched.TComponent;
import tocraft.craftedcore.platform.PlayerProfile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SkinShifterCommand implements CommandEvents.CommandRegistration {
    //#if MC>1182
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection selection) {
        onRegister(dispatcher);
    }
    //#else
    //$$ @Override
    //$$ public void register(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection) {
    //$$     onRegister(dispatcher);
    //$$ }
    //#endif

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
                                            throw new SimpleCommandExceptionType(TComponent.translatable("craftedcore.command.invalid_perms")).create();
                                        }
                                    } else if (!context.getSource().hasPermission(SkinShifter.CONFIG.baseCommandOPLevel)) {
                                        throw new SimpleCommandExceptionType(TComponent.translatable("craftedcore.command.invalid_perms")).create();
                                    }
                                    UUID playerUUID = UuidArgument.getUuid(context, "playerUUID");
                                    SkinShifter.setSkin(player, playerUUID);
                                    // run async in case of bad internet connection
                                    CompletableFuture.runAsync(() -> CCommandSourceStack.sendSuccess(context.getSource(), TComponent.translatable("skinshifter.command.set", player.getName(), PlayerProfile.ofId(playerUUID).name()), true));
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
                                            throw new SimpleCommandExceptionType(TComponent.translatable("craftedcore.command.invalid_perms")).create();
                                        }
                                    } else if (!context.getSource().hasPermission(SkinShifter.CONFIG.baseCommandOPLevel)) {
                                        throw new SimpleCommandExceptionType(TComponent.translatable("craftedcore.command.invalid_perms")).create();
                                    }
                                    String playerName = MessageArgument.getMessage(context, "playerName").getString();
                                    // run async in case of bad internet connection
                                    CompletableFuture.runAsync(() -> {
                                        PlayerProfile playerProfile = PlayerProfile.ofName(playerName);
                                        if (playerProfile == null) {
                                            CCommandSourceStack.sendSuccess(context.getSource(), TComponent.translatable("skinshifter.invalid_player", playerName), true);
                                        } else {
                                            SkinShifter.setSkin(player, playerProfile.id());
                                            CCommandSourceStack.sendSuccess(context.getSource(), TComponent.translatable("skinshifter.command.set", player.getName(), playerName), true);
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
                                    throw new SimpleCommandExceptionType(TComponent.translatable("craftedcore.command.invalid_perms")).create();
                                }
                            } else if (!context.getSource().hasPermission(SkinShifter.CONFIG.baseCommandOPLevel)) {
                                throw new SimpleCommandExceptionType(TComponent.translatable("craftedcore.command.invalid_perms")).create();
                            }
                            SkinShifter.setSkin(player, null);
                            CCommandSourceStack.sendSuccess(context.getSource(), TComponent.translatable("skinshifter.command.reset", player.getName()), true);
                            return 1;
                        })).build();

        LiteralCommandNode<CommandSourceStack> changeChatName = Commands.literal("changeChatName").requires(source -> source.hasPermission(SkinShifter.CONFIG.baseCommandOPLevel))
                .executes(context -> {
                    boolean bool = SkinShifter.CONFIG.changeName;
                    CCommandSourceStack.sendSuccess(context.getSource(), TComponent.translatable("craftedcore.config.get", "changeChatName", String.valueOf(bool)), true);
                    return 1;
                })
                .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(context -> {
                            boolean bool = BoolArgumentType.getBool(context, "value");
                            SkinShifter.CONFIG.changeName = bool;
                            SkinShifter.CONFIG.save();
                            SkinShifter.CONFIG.sendToAllPlayers(context.getSource().getLevel());
                            CCommandSourceStack.sendSuccess(context.getSource(), TComponent.translatable("craftedcore.config.set", "changeChatName", String.valueOf(bool)), true);
                            return 1;
                })).build();

        rootNode.addChild(set);
        rootNode.addChild(reset);
        rootNode.addChild(changeChatName);

        dispatcher.getRoot().addChild(rootNode);
    }
}
