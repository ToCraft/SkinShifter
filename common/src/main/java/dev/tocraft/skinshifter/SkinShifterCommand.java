package dev.tocraft.skinshifter;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.tocraft.craftedcore.event.common.CommandEvents;
import dev.tocraft.skinshifter.data.SkinPlayerData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
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
                                    SkinShifter.setSkinURI(player, null, false); // reset Skin URI
                                    SkinShifter.setSkin(player, playerUUID);
                                    // run async in case of bad internet connection
                                    SkinPlayerData.getSkinProfile(playerUUID).thenAccept(profile -> context.getSource().sendSuccess(() -> Component.translatable("skinshifter.command.set", player.getName(), profile.orElse(player.getGameProfile()).getName()), true));
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
                                            context.getSource().sendSuccess(() -> Component.translatable("skinshifter.invalid_player", playerName), true);
                                        } else {
                                            SkinShifter.setSkin(player, profile.get().getId());
                                            context.getSource().sendSuccess(() -> Component.translatable("skinshifter.command.set", player.getName(), playerName), true);
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
                            // reset skin & skin uri
                            SkinShifter.setSkin(player, null);
                            SkinShifter.setSkinURI(player, null, false);
                            context.getSource().sendSuccess(() -> Component.translatable("skinshifter.command.reset", player.getName()), true);
                            return 1;
                        })).build();

        LiteralCommandNode<CommandSourceStack> changeChatName = Commands.literal("changeChatName").requires(source -> source.hasPermission(SkinShifter.CONFIG.baseCommandOPLevel))
                .executes(context -> {
                    boolean bool = SkinShifter.CONFIG.changeName;
                    context.getSource().sendSuccess(() -> Component.translatable("craftedcore.config.get", "changeChatName", String.valueOf(bool)), true);
                    return 1;
                })
                .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(context -> {
                            boolean bool = BoolArgumentType.getBool(context, "value");
                            SkinShifter.CONFIG.changeName = bool;
                            SkinShifter.CONFIG.save();
                            SkinShifter.CONFIG.sendToAllPlayers(context.getSource().getLevel());
                            context.getSource().sendSuccess(() -> Component.translatable("craftedcore.config.set", "changeChatName", String.valueOf(bool)), true);
                            return 1;
                        })).build();

        LiteralCommandNode<CommandSourceStack> setURI = Commands.literal("url")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("slim", BoolArgumentType.bool())
                                .then(Commands.argument("uri", StringArgumentType.greedyString())
                                        .executes(SkinShifterCommand::setByURI)))).build();

        rootNode.addChild(set);
        rootNode.addChild(reset);
        rootNode.addChild(changeChatName);
        rootNode.addChild(setURI);

        dispatcher.getRoot().addChild(rootNode);
    }

    private static int setByURI(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

        String uriStr = StringArgumentType.getString(context, "uri");
        boolean slim = BoolArgumentType.getBool(context, "slim");

        try {
            URI uri = new URI(uriStr);

            SkinShifter.setSkin(player, null); // reset UUID skin
            SkinShifter.setSkinURI(player, uriStr, slim);
            Component uriText = Component.literal(uriStr).withStyle(Style.EMPTY
                    .withColor(ChatFormatting.AQUA)
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent.OpenUrl(uri)));
            context.getSource().sendSuccess(() -> Component.translatable("skinshifter.command.set", player.getName(), uriText), true);
            return 1;
        } catch (URISyntaxException e) {
            context.getSource().sendFailure(Component.translatable("skinshifter.command.invalid_uri"));
            return 0;
        }
    }
}
