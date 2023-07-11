package vanadium;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSources;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypeRegistrar;
import net.minecraft.world.dimension.DimensionTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Vanadium implements ClientModInitializer {
    private static final Logger _logger = LogManager.getLogger(Vanadium.class);
    public static final String MODID = "vanadium";
    public static final Identifier OVERWORLD_ID = DimensionTypes.OVERWORLD_ID;

    private static final VanadiumConfig config = new VanadiumConfig();

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

        Identifier biomeId = ;

        if(biomeId == null){
            return BiomeKeys.PLAINS.getValue();
        }

        return biomeId;
    }

    @Override
    public void onInitializeClient() {

    }
}
