package dev.tocraft.skinshifter.mixin;

import dev.tocraft.skinshifter.SkinShifter;
import dev.tocraft.skinshifter.data.SkinPlayerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.craftedcore.patched.TComponent;
import tocraft.craftedcore.platform.PlayerProfile;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow protected abstract MutableComponent decorateDisplayNameComponent(MutableComponent displayName);

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void onGetName(CallbackInfoReturnable<Component> cir) {
        if (SkinShifter.CONFIG.changeChatName) {
            PlayerProfile skin = SkinPlayerData.getSkin((Player) (Object) this);
            if (skin != null) {
                Component skinName = decorateDisplayNameComponent(TComponent.literal(skin.name()));
                cir.setReturnValue(skinName);
            }
        }
    }
}
