package vanadium.utils;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.util.Identifier;
import vanadium.Vanadium;
import vanadium.customcolors.resources.*;

import static vanadium.Vanadium.MODID;

public final class VanadiumColormaticResolution {
    private VanadiumColormaticResolution(){}
    public static final BiomeColorMappingResource WATER_COLORS = new BiomeColorMappingResource(new Identifier(MODID, "colormap/water"));
    public static final BiomeColorMappingResource UNDERWATER_COLORS = new BiomeColorMappingResource(new Identifier(MODID, "colormap/underwater"));
    public static final BiomeColorMappingResource UNDERLAVA_COLORS = new BiomeColorMappingResource(new Identifier(MODID, "colormap/underlava"));
    public static final BiomeColorMappingResource SKY_COLORS = new BiomeColorMappingResource(new Identifier(MODID, "colormap/sky0"));
    public static final BiomeColorMappingResource FOG_COLORS = new BiomeColorMappingResource(new Identifier(MODID, "colormap/fog0"));
    public static final BiomeColorMappingResource BIRCH_COLORS = new BiomeColorMappingResource(new Identifier(MODID, "colormap/birch"));
    public static final BiomeColorMappingResource SPRUCE_COLORS = new BiomeColorMappingResource(new Identifier(MODID, "colormap/pine"));
    public static final LinearColorMappingResource PUMPKIN_STEM_COLORS = new LinearColorMappingResource(new Identifier(MODID, "colormap/pumpkinstem.png"));
    public static final LinearColorMappingResource MELON_STEM_COLORS = new LinearColorMappingResource(new Identifier(MODID, "colormap/melonstem.png"));
    public static final LinearColorMappingResource REDSTONE_COLORS = new LinearColorMappingResource(new Identifier(MODID, "colormap/redstone.png"));
    public static final LinearColorMappingResource MYCELIUM_PARTICLE_COLORS = new LinearColorMappingResource(new Identifier(MODID, "colormap/myceliumparticle.png"));
    public static final LinearColorMappingResource LAVA_DROP_COLORS = new LinearColorMappingResource(new Identifier(MODID, "colormap/lavadrop.png"));
    public static final LinearColorMappingResource DURABILITY_COLORS = new LinearColorMappingResource(new Identifier(MODID, "colormap/durability.png"));
    public static final LinearColorMappingResource EXPERIENCE_ORB_COLORS = new LinearColorMappingResource(new Identifier(MODID, "colormap/xporb.png"));
    public static final CustomBiomeColorMappingResource CUSTOM_BLOCK_COLORS = new CustomBiomeColorMappingResource();
    public static final GlobalLightMappingResource LIGHTMAP_PROPERTIES = new GlobalLightMappingResource(new Identifier(MODID, "lightmap.json"));
    public static final LightMappingResource LIGHTMAPS = new LightMappingResource(new Identifier(MODID, "lightmap"));
    public static final GlobalColorResource COLOR_PROPERTIES = new GlobalColorResource(new Identifier(MODID, "color"));
    public static final BiomeColorMappingResource COLORMATIC_WATER_COLORS = new BiomeColorMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "colormap/water"));
    public static final BiomeColorMappingResource COLORMATIC_UNDERWATER_COLORS = new BiomeColorMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "colormap/underwater"));
    public static final BiomeColorMappingResource COLORMATIC_UNDERLAVA_COLORS = new BiomeColorMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "colormap/underlava"));
    public static final BiomeColorMappingResource COLORMATIC_SKY_COLORS = new BiomeColorMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "colormap/sky0"));
    public static final BiomeColorMappingResource COLORMATIC_FOG_COLORS = new BiomeColorMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "colormap/fog0"));
    public static final BiomeColorMappingResource COLORMATIC_BIRCH_COLORS = new BiomeColorMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "colormap/birch"));
    public static final BiomeColorMappingResource COLORMATIC_SPRUCE_COLORS = new BiomeColorMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "colormap/pine"));
    public static final LinearColorMappingResource COLORMATIC_PUMPKIN_STEM_COLORS = new LinearColorMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "colormap/pumpkinstem.png"));
    public static final LinearColorMappingResource COLORMATIC_MELON_STEM_COLORS = new LinearColorMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "colormap/melonstem.png"));
    public static final LinearColorMappingResource COLORMATIC_REDSTONE_COLORS = new LinearColorMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "colormap/redstone.png"));
    public static final LinearColorMappingResource COLORMATIC_MYCELIUM_PARTICLE_COLORS = new LinearColorMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "colormap/myceliumparticle.png"));
    public static final LinearColorMappingResource COLORMATIC_LAVA_DROP_COLORS = new LinearColorMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "colormap/lavadrop.png"));
    public static final LinearColorMappingResource COLORMATIC_DURABILITY_COLORS = new LinearColorMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "colormap/durability.png"));
    public static final LinearColorMappingResource COLORMATIC_EXPERIENCE_ORB_COLORS = new LinearColorMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "colormap/xporb.png"));
    public static final CustomBiomeColorMappingResource COLORMATIC_CUSTOM_BLOCK_COLORS = new CustomBiomeColorMappingResource();
    public static final GlobalLightMappingResource COLORMATIC_LIGHTMAP_PROPERTIES = new GlobalLightMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "lightmap.json"));
    public static final LightMappingResource COLORMATIC_LIGHTMAPS = new LightMappingResource(new Identifier(Vanadium.COLORMATIC_ID, "lightmap"));
    public static final GlobalColorResource COLORMATIC_COLOR_PROPERTIES = new GlobalColorResource(new Identifier(Vanadium.COLORMATIC_ID, "color"));

    public static void registerResources(ResourceManagerHelper client) {
        client.registerReloadListener(WATER_COLORS);
        client.registerReloadListener(UNDERWATER_COLORS);
        client.registerReloadListener(UNDERLAVA_COLORS);
        client.registerReloadListener(SKY_COLORS);
        client.registerReloadListener(FOG_COLORS);
        client.registerReloadListener(BIRCH_COLORS);
        client.registerReloadListener(SPRUCE_COLORS);
        client.registerReloadListener(REDSTONE_COLORS);
        client.registerReloadListener(PUMPKIN_STEM_COLORS);
        client.registerReloadListener(MELON_STEM_COLORS);
        client.registerReloadListener(MYCELIUM_PARTICLE_COLORS);
        client.registerReloadListener(LAVA_DROP_COLORS);
        client.registerReloadListener(DURABILITY_COLORS);
        client.registerReloadListener(EXPERIENCE_ORB_COLORS);
        client.registerReloadListener(LIGHTMAP_PROPERTIES);
        client.registerReloadListener(LIGHTMAPS);

        client.registerReloadListener(COLORMATIC_WATER_COLORS);
        client.registerReloadListener(COLORMATIC_UNDERWATER_COLORS);
        client.registerReloadListener(COLORMATIC_UNDERLAVA_COLORS);
        client.registerReloadListener(COLORMATIC_SKY_COLORS);
        client.registerReloadListener(COLORMATIC_FOG_COLORS);
        client.registerReloadListener(COLORMATIC_BIRCH_COLORS);
        client.registerReloadListener(COLORMATIC_SPRUCE_COLORS);
        client.registerReloadListener(COLORMATIC_REDSTONE_COLORS);
        client.registerReloadListener(COLORMATIC_PUMPKIN_STEM_COLORS);
        client.registerReloadListener(COLORMATIC_MELON_STEM_COLORS);
        client.registerReloadListener(COLORMATIC_MYCELIUM_PARTICLE_COLORS);
        client.registerReloadListener(COLORMATIC_LAVA_DROP_COLORS);
        client.registerReloadListener(COLORMATIC_DURABILITY_COLORS);
        client.registerReloadListener(COLORMATIC_EXPERIENCE_ORB_COLORS);
        client.registerReloadListener(COLORMATIC_LIGHTMAP_PROPERTIES);
        client.registerReloadListener(COLORMATIC_LIGHTMAPS);
    }
}
