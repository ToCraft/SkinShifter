package dev.tocraft.skinshifter;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.tocraft.skinshifter.data.SkinPlayerData;
//#if MC>1182
import net.minecraft.commands.CommandBuildContext;
//#endif
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.server.level.ServerPlayer;
import tocraft.craftedcore.event.common.CommandEvents;
import tocraft.craftedcore.patched.CCommandSourceStack;
import tocraft.craftedcore.patched.TComponent;
import tocraft.craftedcore.platform.PlayerProfile;

import java.util.Objects;
import java.util.UUID;

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

    private void onRegister(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> rootNode = Commands.literal(SkinShifter.MODID).requires(source -> source.hasPermission(2)).build();
        LiteralCommandNode<CommandSourceStack> set = Commands.literal("set")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("playerUUID", UuidArgument.uuid())
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    UUID playerUUID = UuidArgument.getUuid(context, "playerUUID");
                                    SkinShifter.setSkin(player, playerUUID);
                                    CCommandSourceStack.sendSuccess(context.getSource(), TComponent.translatable("skinshifter.command.set", player.getDisplayName(), PlayerProfile.ofId(playerUUID).name()), true);
                                    return 1;
                                }))
                        .then(Commands.argument("playerName", MessageArgument.message())
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    String playerName = MessageArgument.getMessage(context, "playerName").getString();
                                    PlayerProfile playerProfile = PlayerProfile.ofName(playerName);
                                    if (playerProfile == null) {
                                        CCommandSourceStack.sendSuccess(context.getSource(), TComponent.translatable("skinshifter.invalid_player", playerName), true);
                                        return 0;
                                    }
                                    SkinShifter.setSkin(player, playerProfile.id());
                                    CCommandSourceStack.sendSuccess(context.getSource(), TComponent.translatable("skinshifter.command.set", player.getDisplayName(), playerName), true);
                                    return 1;
                                }))).build();

        LiteralCommandNode<CommandSourceStack> reset = Commands.literal("reset")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            SkinShifter.setSkin(player, null);
                            CCommandSourceStack.sendSuccess(context.getSource(), TComponent.translatable("skinshifter.command.reset", player.getDisplayName()), true);
                            return 1;
                        })).build();

        rootNode.addChild(set);
        rootNode.addChild(reset);

        dispatcher.getRoot().addChild(rootNode);
    }
}
