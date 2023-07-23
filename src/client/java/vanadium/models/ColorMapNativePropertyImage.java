package vanadium.models;

import com.mojang.blaze3d.platform.NativeImage;
import vanadium.properties.ColorMappingProperties;

public record ColorMapNativePropertyImage(ColorMappingProperties colormapProperties, NativeImage nativeImage) {
}
