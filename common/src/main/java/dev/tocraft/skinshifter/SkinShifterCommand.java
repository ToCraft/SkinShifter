package dev.tocraft.skinshifter;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.tocraft.skinshifter.data.SkinPlayerData;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.world.entity.player.Player;
import tocraft.craftedcore.event.common.CommandEvents;
import tocraft.craftedcore.patched.CCommandSourceStack;
import tocraft.craftedcore.patched.TComponent;
import tocraft.craftedcore.platform.PlayerProfile;

import java.util.Objects;
import java.util.UUID;

public class SkinShifterCommand implements CommandEvents.CommandRegistration {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection selection) {
        onRegister(dispatcher);
    }

    private void onRegister(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> rootNode = Commands.literal(SkinShifter.MODID).requires(source -> source.hasPermission(2)).build();
        LiteralCommandNode<CommandSourceStack> set = Commands.literal("set")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("playerUUID", UuidArgument.uuid())
                                .executes(context -> {
                                    Player player = EntityArgument.getPlayer(context, "player");
                                    UUID playerUUID = UuidArgument.getUuid(context, "playerUUID");
                                    SkinPlayerData.setSkin(player, playerUUID);
                                    CCommandSourceStack.sendSuccess(context.getSource(), TComponent.translatable("skinshifter.command.set", player.getDisplayName(), PlayerProfile.ofId(playerUUID).name()), true);
                                    return 1;
                                }))
                        .then(Commands.argument("playerName", MessageArgument.message())
                                .executes(context -> {
                                    Player player = EntityArgument.getPlayer(context, "player");
                                    String playerName = MessageArgument.getMessage(context, "playerName").getString();
                                    SkinPlayerData.setSkin(player, Objects.requireNonNull(PlayerProfile.ofName(playerName)).id());
                                    CCommandSourceStack.sendSuccess(context.getSource(), TComponent.translatable("skinshifter.command.set", player.getDisplayName(), playerName), true);
                                    return 1;
                                }))).build();

        LiteralCommandNode<CommandSourceStack> reset = Commands.literal("reset")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            Player player = EntityArgument.getPlayer(context, "player");
                            SkinPlayerData.setSkin(player, null);
                            CCommandSourceStack.sendSuccess(context.getSource(), TComponent.translatable("skinshifter.command.reset", player.getDisplayName()), true);
                            return 1;
                        })).build();

        rootNode.addChild(set);
        rootNode.addChild(reset);

        dispatcher.getRoot().addChild(rootNode);
    }
}
