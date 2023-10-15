package vanadium.utils;

import com.google.common.collect.Lists;
import com.mojang.serialization.Lifecycle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vanadium.Vanadium;

import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
public final class SimpleBiomeRegistryUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Vanadium.class);

    @Getter
    private static final List<ResourceKey<Biome>> BIOME_KEYS;

    @Getter
    private static final MappedRegistry<Biome> BUILT_IN_BIOMES;

    @Getter
    @Setter
    private static @Nullable RegistryAccess dynamicAccess;

    static {
        BIOME_KEYS = Lists.newArrayList(
                Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.WINDSWEPT_HILLS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.NETHER_WASTES, Biomes.THE_END, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_PLAINS, Biomes.MUSHROOM_FIELDS, Biomes.BEACH, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.DEEP_OCEAN, Biomes.STONY_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.WINDSWEPT_FOREST, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_MIDLANDS, Biomes.END_HIGHLANDS, Biomes.END_BARRENS, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.THE_VOID, Biomes.SUNFLOWER_PLAINS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.FLOWER_FOREST, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.WINDSWEPT_SAVANNA, Biomes.ERODED_BADLANDS, Biomes.BAMBOO_JUNGLE, Biomes.SOUL_SAND_VALLEY, Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST, Biomes.BASALT_DELTAS, Biomes.DRIPSTONE_CAVES, Biomes.LUSH_CAVES, Biomes.MEADOW, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.MANGROVE_SWAMP, Biomes.DEEP_DARK, Biomes.CHERRY_GROVE);
    }

    static {
        var builtIn = VanillaRegistries
                .createLookup()
                .asGetterLookup()
                .lookupOrThrow(Registries.BIOME);

        var builtInBiomeRegistry = new MappedRegistry<>(Registries.BIOME, Lifecycle.stable());

        BIOME_KEYS
                .forEach(
                        b -> builtInBiomeRegistry.register(b, builtIn.getOrThrow(b).value(), Lifecycle.stable())
                );

        BUILT_IN_BIOMES = builtInBiomeRegistry;
    }

    public static int getBiomeRawId(ResourceKey<Biome> biomeRegistryKey) {

        if(dynamicAccess != null) {
            var biome = dynamicAccess.registryOrThrow(Registries.BIOME)
                    .getHolder(biomeRegistryKey)
                    .get();
           return dynamicAccess.registryOrThrow(Registries.BIOME)
                    .asHolderIdMap()
                    .getId(biome);
        }

        var lookedUpBiome =BUILT_IN_BIOMES
                .createRegistrationLookup()
                .get(biomeRegistryKey);

        if(lookedUpBiome.isEmpty()) {
            var biomeRegistryHolder = VanillaRegistries
                    .createLookup()
                    .asGetterLookup()
                    .lookupOrThrow(Registries.BIOME);

            lookedUpBiome = biomeRegistryHolder.get(biomeRegistryKey);

            BUILT_IN_BIOMES
                    .register(biomeRegistryKey, biomeRegistryHolder.getOrThrow(biomeRegistryKey).value(), Lifecycle.stable());
        }

        return getBiomeRawId(lookedUpBiome.get().value());
    }

    private static int getBiomeRawId(Biome biome) {
        return SimpleBiomeRegistryUtils.BUILT_IN_BIOMES.getId(biome);
    }

}

