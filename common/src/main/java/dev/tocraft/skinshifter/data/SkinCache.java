package dev.tocraft.skinshifter.data;

import com.mojang.blaze3d.platform.NativeImage;
import dev.tocraft.skinshifter.SkinShifter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.patched.Identifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Environment(EnvType.CLIENT)
public class SkinCache {
    private static final Map<String, ResourceLocation> SKIN_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, ResourceLocation> CAPE_CACHE = new ConcurrentHashMap<>();

    public static ResourceLocation getCustomSkinId(URL skinUrl) {
        return SKIN_CACHE.computeIfAbsent(String.valueOf(skinUrl), key -> {
            ResourceLocation id = Identifier.parse(SkinShifter.MODID, "textures/entity/custom_skin_" + key.hashCode() + ".png");
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

    @Nullable
    public static ResourceLocation getCustomCapeId(@Nullable URL capeUrl) {
        if (capeUrl == null) {
            return null;
        }
        return CAPE_CACHE.computeIfAbsent(String.valueOf(capeUrl), key -> {
            ResourceLocation id = Identifier.parse(SkinShifter.MODID, "textures/entity/custom_cape_" + key.hashCode() + ".png");
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
