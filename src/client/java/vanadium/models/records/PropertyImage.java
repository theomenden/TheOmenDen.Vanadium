package vanadium.models.records;

import net.minecraft.client.texture.NativeImage;
import vanadium.models.ColorMappingProperties;

public record PropertyImage(ColorMappingProperties properties, NativeImage nativeImage) {
}
