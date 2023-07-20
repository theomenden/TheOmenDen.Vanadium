package vanadium;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vanadium.resources.*;

public class Vanadium implements ClientModInitializer {
    private static final Logger LOGGER = LogManager.getLogger(Vanadium.class);
    public static final String MODID = "vanadium";
    public static final String COLORMATIC_ID = "colormatic";
    public static final Identifier OVERWORLD_ID = DimensionTypes.OVERWORLD_ID;

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
    private static VanadiumConfig config = new VanadiumConfig();

    public static VanadiumConfig getCurrentConfiguration() {
        return config;
    }

    public static Identifier getDimensionId(World world) {
        Identifier dimensionId = world.getDimensionKey().getValue();

        if(dimensionId == null){
            return OVERWORLD_ID;
        }

        return dimensionId;
    }

    public static Identifier getBiomeId(DynamicRegistryManager manager, Biome biome) {

        Identifier biomeId = manager.get(RegistryKeys.BIOME).getId(biome);

        if(biomeId == null){
            return BiomeKeys.PLAINS.getValue();
        }

        return biomeId;
    }

    public static RegistryKey<Biome> getBiomeKey(DynamicRegistryManager manager, Biome biome) {
        return manager.get(RegistryKeys.BIOME).getKey(biome).orElse(BiomeKeys.PLAINS);
    }

    public static <T> T getRegistryValue(Registry<T> registry, RegistryEntry<T> entry) {
        var optionalRegistryKey = entry.getKey();
        if(optionalRegistryKey.isPresent()) {
            return registry.get(optionalRegistryKey.get());
        }
        return entry.value();
    }

    @Override
    public void onInitializeClient() {
        VanadiumConfig.deserializeConfigFromJsonAsync()
                .thenAccept((deserializedConfig) -> {
                    LOGGER.info("Vanadium configuration initialized...");
                    config = deserializedConfig;
                }).exceptionally(ex -> {
                    LOGGER.error("Vanadium configuration failed to initialize, using default configuration:  {}...", ex.getMessage());
                    config = VanadiumConfig.INSTANCE;
                    return null;
                }).join();

        ResourceManagerHelper client = ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES);
        client.registerReloadListener(WATER_COLORS);
        client.registerReloadListener(UNDERWATER_COLORS);
        client.registerReloadListener(UNDERLAVA_COLORS);
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

        LOGGER.info("Vanadium initialized!");

    }
}
