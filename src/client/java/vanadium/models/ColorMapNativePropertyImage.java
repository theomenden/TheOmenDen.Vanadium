package vanadium.models;

import vanadium.properties.ColorMappingProperties;
import net.minecraft.client.texture.NativeImage;

public record ColorMapNativePropertyImage(ColorMappingProperties colormapProperties, NativeImage nativeImage) {
}
