package dev.tocraft.skinshifter.mixin.client;

import dev.tocraft.skinshifter.SkinShifter;
import dev.tocraft.skinshifter.data.ShiftPlayerSkin;
import dev.tocraft.skinshifter.data.SkinCache;
import dev.tocraft.skinshifter.data.SkinPlayerData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
//#if MC>1201
import net.minecraft.client.resources.PlayerSkin;
//#endif
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {
    //#if MC>1201
    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    public void setToNewSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        ShiftPlayerSkin skin = SkinPlayerData.getSkin((Player) (Object) this);
        if (skin != null && skin.skin() != null) {
            ResourceLocation skinId = SkinCache.getCustomSkinId(skin.skin());
            ResourceLocation capeId = SkinShifter.CONFIG.changeCape ? SkinCache.getCustomCapeId(skin.cape()) : null;
            PlayerSkin.Model model = skin.isSlim() ? PlayerSkin.Model.SLIM : PlayerSkin.Model.WIDE;
            PlayerSkin playerSkin = new PlayerSkin(skinId, skin.skin().toString(), capeId, null, model, true);
            cir.setReturnValue(playerSkin);
        }
    }
    //#else
    //$$ @Inject(method = "getSkinTextureLocation", at = @At("RETURN"), cancellable = true)
    //$$ public void setToNewSkin(CallbackInfoReturnable<ResourceLocation> cir) {
    //$$     ShiftPlayerSkin skin = SkinPlayerData.getSkin((Player) (Object) this);
    //$$     if (skin != null && skin.skin() != null) {
    //$$         cir.setReturnValue(SkinCache.getCustomSkinId(skin.skin()));
    //$$     }
    //$$ }
    //$$ @Inject(method = "getModelName", at = @At("RETURN"), cancellable = true)
    //$$ public void setModelType(CallbackInfoReturnable<String> cir) {
    //$$     ShiftPlayerSkin skin = SkinPlayerData.getSkin((Player) (Object) this);
    //$$     if (null != skin && skin.skin() != null) {
    //$$         cir.setReturnValue(skin.isSlim() ? "slim" : "default");
    //$$     }
    //$$ }
    //$$ @Inject(method = "getCloakTextureLocation", at = @At("RETURN"), cancellable = true)
    //$$ public void setToNewCloak(CallbackInfoReturnable<ResourceLocation> cir) {
    //$$     if (SkinShifter.CONFIG.changeCape) {
    //$$         ShiftPlayerSkin skin = SkinPlayerData.getSkin((Player) (Object) this);
    //$$         if (skin != null && skin.cape() != null) {
    //$$             cir.setReturnValue(SkinCache.getCustomCapeId(skin.cape()));
    //$$         }
    //$$     }
    //$$ }
    //#endif
}
