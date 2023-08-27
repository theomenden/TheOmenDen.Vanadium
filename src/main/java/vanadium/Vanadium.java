package vanadium;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vanadium.customcolors.resources.GlobalColorResource;
import vanadium.utils.VanadiumColormaticResolution;

@Environment(EnvType.CLIENT)
public class Vanadium implements ClientModInitializer {
    private static final Logger logger = LogManager.getLogger(Vanadium.class);
    public static VanadiumConfig configuration;
    public static final String MODID = "vanadium";
    public static final String COLORMATIC_ID = "colormatic";
    public static final Identifier OVERWORLD_ID = Identifier.of("minecraft", "overworld");

    public static final GlobalColorResource COLOR_PROPERTIES = new GlobalColorResource(new Identifier(MODID, "color"));

    public static Identifier getDimensionid(World world) {
        DimensionType type = world.getDimension();

        Identifier id = world.getRegistryManager()
                .get(RegistryKeys.DIMENSION_TYPE)
                .getId(type);

        if(id == null) {
            id = OVERWORLD_ID;
        }

        return id;
    }

    public static RegistryKey<Biome> getBiomeRegistryKey(DynamicRegistryManager manager, Biome biome) {
        return manager.get(RegistryKeys.BIOME)
                      .getKey(biome)
                      .orElse(BiomeKeys.PLAINS);
    }

    public static Identifier getBiomeIdentifier(DynamicRegistryManager manager, Biome biome) {
        var id = manager.get(RegistryKeys.BIOME)
                .getId(biome);

        if(id == null) {
            id = BiomeKeys.PLAINS.getValue();
        }
        return id;
    }

    public static <T> T getRegistryValue(Registry<T> registry, RegistryEntry<T> entry) {
        var keyHolder = entry.getKey();

        if(keyHolder.isPresent()) {
            return registry.get(keyHolder.get());
        }

        return entry.value();
    }

    @Override
    public void onInitializeClient() {
        AutoConfig.register(VanadiumConfig.class, GsonConfigSerializer::new);
        configuration = AutoConfig
                .getConfigHolder(VanadiumConfig.class)
                .getConfig();

        ResourceManagerHelper client = ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES);
        VanadiumColormaticResolution.registerResources(client);
        logger.info("Vanadium initialized, we're adding some color to your world");
    }
}
