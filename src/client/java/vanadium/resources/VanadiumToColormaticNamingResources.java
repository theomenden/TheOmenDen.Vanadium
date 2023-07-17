package vanadium.resources;

import net.minecraft.util.Identifier;

public final class VanadiumToColormaticNamingResources {
    private static final String COLORMATIC_ID = "colormatic";
    private static final String VANADIUM_ID = "vanadium";

    public static final BiomeColorMappingResource WATER_COLORS = new BiomeColorMappingResource(new Identifier(COLORMATIC_ID, "colormap/water"));
}
