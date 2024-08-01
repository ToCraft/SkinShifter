package dev.tocraft.skinshifter.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tocraft.skinshifter.SkinShifter;
import dev.tocraft.skinshifter.data.ShiftPlayerSkin;
import dev.tocraft.skinshifter.data.SkinPlayerData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @ModifyVariable(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Component modifyNameTag(Component component, @Local(ordinal = 0, argsOnly = true) AbstractClientPlayer abstractClientPlayer) {
        if (SkinShifter.CONFIG.changeNameTag) {
            ShiftPlayerSkin skin = SkinPlayerData.getSkin(abstractClientPlayer);
            if (skin != null && skin.displayName() != null) {
                return skin.displayName();
            }
        }
        return component;
    }
}
