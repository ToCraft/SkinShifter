package dev.tocraft.skinshifter.data;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import dev.tocraft.skinshifter.SkinShifter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class TextureCache {
    private static final Map<String, Optional<ResourceLocation>> LOADED_TEXTURES = new ConcurrentHashMap<>();
    private static String failedUrl = "";

    public static Optional<ResourceLocation> getSkinTextureId(@NotNull URL textureURL) {
        String urlString = textureURL.toString();

        return LOADED_TEXTURES.computeIfAbsent(urlString, url -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    SkinShifter.MODID,
                    "textures/player/skin_" + url.hashCode() + ".png"
            );

            try (InputStream is = textureURL.openStream()) {
                NativeImage image = NativeImage.read(new ByteArrayInputStream(is.readAllBytes()));

                DynamicTexture dynamicTexture = new DynamicTexture(id::toString, image);

                Minecraft.getInstance().getTextureManager().register(id, dynamicTexture);
                return Optional.of(id); // success
            } catch (IOException e) {
                if (!Objects.equals(failedUrl, url)) { // only log once
                    LogUtils.getLogger().error("Failed to load texture from URL: {}", url, e);
                    // show in chat
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.displayClientMessage(Component.translatable("skinshifter.command.invalid_uri").withColor(0xFF0000), false);
                    }

                    failedUrl = url;
                }
                return Optional.empty(); // permanently cache the failure
            }
        });
    }
}
