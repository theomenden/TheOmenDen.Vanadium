package vanadium;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
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

public class VanadiumClient implements ClientModInitializer {
public static VanadiumConfig configuration;
    public static final String MODID = "vanadium";
    public static final String COLORMATIC_ID = "colormatic";
    public static final Identifier OVERWORLD_ID = new Identifier("minecraft:overworld");



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
        return manager.get(RegistryKeys.BIOME).getKey(biome).orElse(BiomeKeys.PLAINS);
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
        configuration = AutoConfig.getConfigHolder(VanadiumConfig.class)
                .getConfig();

        ResourceManagerHelper client = ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES);

    }
}
