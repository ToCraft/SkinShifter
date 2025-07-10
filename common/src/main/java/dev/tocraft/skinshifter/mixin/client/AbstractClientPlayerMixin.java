package dev.tocraft.skinshifter.mixin.client;

import dev.tocraft.skinshifter.data.SkinPlayerData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {
    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    public void setToNewSkin(@NotNull CallbackInfoReturnable<PlayerSkin> cir) {
        @NotNull CompletableFuture<Optional<PlayerSkin>> skinFuture = SkinPlayerData.getPlayerSkin((Player) (Object) this);
        Optional<PlayerSkin> playerSkin = skinFuture.getNow(Optional.empty());
        playerSkin.ifPresent(cir::setReturnValue);
    }
}
