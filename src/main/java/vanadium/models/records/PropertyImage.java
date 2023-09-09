package vanadium.models.records;

import com.mojang.blaze3d.platform.NativeImage;
import vanadium.models.ColorMappingProperties;

public record PropertyImage(ColorMappingProperties properties, NativeImage nativeImage) {
}
