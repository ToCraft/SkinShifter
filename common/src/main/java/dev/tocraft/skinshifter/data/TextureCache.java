package dev.tocraft.skinshifter.data;

import com.mojang.logging.LogUtils;
import dev.tocraft.skinshifter.SkinShifter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class TextureCache {
    private static final Map<String, Optional<ResourceLocation>> LOADED_TEXTURES = new ConcurrentHashMap<>();

    public static Optional<ResourceLocation> getSkinTextureId(@NotNull URL textureURL) {
        String urlString = textureURL.toString();

        return LOADED_TEXTURES.computeIfAbsent(urlString, url -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    SkinShifter.MODID,
                    "textures/player/skin_" + url.hashCode() + ".png"
            );

            try (InputStream is = textureURL.openStream()) {
                NativeImage image = NativeImage.read(new ByteArrayInputStream(is.readAllBytes()));

                //#if MC>=1215
                DynamicTexture dynamicTexture = new DynamicTexture(id::toString, image);
                //#else
                //$$ DynamicTexture dynamicTexture = new DynamicTexture(image);
                //#endif

                Minecraft.getInstance().getTextureManager().register(id, dynamicTexture);
                return Optional.of(id); // success
            } catch (IOException e) {
                LogUtils.getLogger().error("Failed to load texture from URL: {}", url, e);
                return Optional.empty(); // permanently cache the failure
            }
        });
    }
}
