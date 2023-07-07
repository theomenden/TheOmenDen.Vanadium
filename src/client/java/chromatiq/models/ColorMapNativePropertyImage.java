package chromatiq.models;

import chromatiq.properties.ColorMappingProperties;
import net.minecraft.client.texture.NativeImage;

public record ColorMapNativePropertyImage(ColorMappingProperties colormapProperties, NativeImage nativeImage) {
}
