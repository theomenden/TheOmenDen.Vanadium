package vanadium;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
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
    public static final String COLORMATIC_ID = "colormatic";
    public static final Identifier OVERWORLD_ID = DimensionTypes.OVERWORLD_ID;
    private static final VanadiumConfig config = VanadiumConfig.DefaultConfiguration;

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

    public static <T> T getRegistryValue(Registry<T> registry, RegistryEntry<T> entry) {
        var optionalRegistryKey = entry.getKey();
        if(optionalRegistryKey.isPresent()) {
            return registry.get(optionalRegistryKey.get());
        }
        return entry.value();
    }

    @Override
    public void onInitializeClient() {

    }
}
