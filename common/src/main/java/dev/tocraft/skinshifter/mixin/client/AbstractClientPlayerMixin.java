package dev.tocraft.skinshifter.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import dev.tocraft.skinshifter.SkinShifter;
import dev.tocraft.skinshifter.data.ShiftPlayerSkin;
import dev.tocraft.skinshifter.data.SkinPlayerData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
//#if MC>1182
import net.minecraft.client.resources.PlayerSkin;
//#endif
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.craftedcore.patched.Identifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {
    @Unique
    private static final Map<String, ResourceLocation> skinShifter$skinCache = new ConcurrentHashMap<>();
    @Unique
    private static final Map<String, ResourceLocation> skinShifter$capeCache = new ConcurrentHashMap<>();

    //#if MC>1182
    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    public void setToNewSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        ShiftPlayerSkin skin = SkinPlayerData.getSkin((Player) (Object) this);
        if (skin != null && skin.skin() != null) {
            ResourceLocation skinId = skinShifter$getCustomSkinId(skin.skin());
            ResourceLocation capeId = SkinShifter.CONFIG.changeCape ? skinShifter$getCustomCapeId(skin.cape()) : null;
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
    //$$         cir.setReturnValue(skinShifter$getCustomSkinId(skin.skin()));
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
    //$$             cir.setReturnValue(skinShifter$getCustomCapeId(skin.cape()));
    //$$         }
    //$$     }
    //$$ }
    //#endif

    @Unique
    private ResourceLocation skinShifter$getCustomSkinId(URL skinUrl) {
        return skinShifter$skinCache.computeIfAbsent(String.valueOf(skinUrl), key -> {
            ResourceLocation id = Identifier.parse("minecraft", "textures/entity/custom_skin_" + key.hashCode() + ".png");
            try(InputStream is = skinUrl.openStream()) {
                NativeImage image = NativeImage.read(new ByteArrayInputStream(is.readAllBytes()));
                DynamicTexture dynamicTexture = new DynamicTexture(image);
                Minecraft.getInstance().getTextureManager().register(id, dynamicTexture);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return id;
        });
    }

    @Unique
    private ResourceLocation skinShifter$getCustomCapeId(URL capeUrl) {
        if (capeUrl == null) {
            return null;
        }
        return skinShifter$capeCache.computeIfAbsent(String.valueOf(capeUrl), key -> {
            ResourceLocation id = Identifier.parse("minecraft", "textures/entity/custom_cape_" + key.hashCode() + ".png");
            try(InputStream is = capeUrl.openStream()) {
                NativeImage image = NativeImage.read(new ByteArrayInputStream(is.readAllBytes()));
                DynamicTexture dynamicTexture = new DynamicTexture(image);
                Minecraft.getInstance().getTextureManager().register(id, dynamicTexture);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return id;
        });
    }
}
