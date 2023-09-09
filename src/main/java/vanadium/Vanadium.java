package vanadium;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vanadium.utils.DebugUtils;
import vanadium.utils.VanadiumColormaticResolution;

@Environment(EnvType.CLIENT)
public class Vanadium implements ClientModInitializer {
    private static final Logger logger = LogManager.getLogger(Vanadium.class);
    public static VanadiumConfig configuration;
    public static final String MODID = "vanadium";
    public static final String COLORMATIC_ID = "colormatic";
    public static final ResourceLocation OVERWORLD_ID = ResourceLocation.tryBuild("minecraft", "overworld");
    public static ResourceLocation getDimensionId(Level level) {
        DimensionType type = level.dimensionType();

        ResourceLocation id = level.registryAccess()
                                   .registryOrThrow(Registries.DIMENSION_TYPE)
                                   .getKey(type);

        if(id == null) {
            id = OVERWORLD_ID;
        }

        return id;
    }

    public static ResourceKey<Biome> getBiomeResourceKey(RegistryAccess registryAccess, Biome biome) {
        return registryAccess.registryOrThrow(Registries.BIOME)
                             .getResourceKey(biome)
                             .orElse(Biomes.PLAINS);
    }

    public static ResourceLocation getBiomeIdentifier(RegistryAccess manager, Biome biome) {
        var resourceLocation = manager
                .registryOrThrow(Registries.BIOME)
                .getKey(biome);

        if(resourceLocation == null) {
            resourceLocation = Biomes.PLAINS.location();
        }

        return resourceLocation;
    }

    public static <T> T getRegistryValue(Registry<T> registry, Holder<T> entry) {
        var keyHolder = entry.unwrapKey();

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

        ResourceManagerHelper client = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
        VanadiumColormaticResolution.registerResources(client);
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
                DebugUtils.registerDebugCommands(dispatcher)));
        logger.info("Vanadium initialized, we're adding some color to your world");
    }
}
