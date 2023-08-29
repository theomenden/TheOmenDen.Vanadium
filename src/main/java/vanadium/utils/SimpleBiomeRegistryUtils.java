package vanadium.utils;

import com.google.common.collect.Lists;
import com.mojang.serialization.Lifecycle;
import lombok.Getter;
import net.minecraft.registry.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import vanadium.mixin.compatibility.SimpleRegistryAccessor;

import java.util.List;

@Getter
public final class SimpleBiomeRegistryUtils {
    @Getter
    private static final List<RegistryKey<Biome>> BIOME_KEYS;

    @Getter
    private static final SimpleRegistry<Biome> BUILT_IN_BIOMES;

    static {
        BIOME_KEYS = Lists.newArrayList(
                BiomeKeys.OCEAN, BiomeKeys.PLAINS, BiomeKeys.DESERT, BiomeKeys.WINDSWEPT_HILLS, BiomeKeys.FOREST, BiomeKeys.TAIGA, BiomeKeys.SWAMP, BiomeKeys.RIVER, BiomeKeys.NETHER_WASTES, BiomeKeys.THE_END, BiomeKeys.FROZEN_OCEAN, BiomeKeys.FROZEN_RIVER, BiomeKeys.SNOWY_PLAINS, BiomeKeys.MUSHROOM_FIELDS, BiomeKeys.BEACH, BiomeKeys.JUNGLE, BiomeKeys.SPARSE_JUNGLE, BiomeKeys.DEEP_OCEAN, BiomeKeys.STONY_SHORE, BiomeKeys.SNOWY_BEACH, BiomeKeys.BIRCH_FOREST, BiomeKeys.DARK_FOREST, BiomeKeys.SNOWY_TAIGA, BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.WINDSWEPT_FOREST, BiomeKeys.SAVANNA, BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.BADLANDS, BiomeKeys.WOODED_BADLANDS, BiomeKeys.SMALL_END_ISLANDS, BiomeKeys.END_MIDLANDS, BiomeKeys.END_HIGHLANDS, BiomeKeys.END_BARRENS, BiomeKeys.WARM_OCEAN, BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.COLD_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN, BiomeKeys.DEEP_COLD_OCEAN, BiomeKeys.DEEP_FROZEN_OCEAN, BiomeKeys.THE_VOID, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, BiomeKeys.FLOWER_FOREST, BiomeKeys.ICE_SPIKES, BiomeKeys.OLD_GROWTH_BIRCH_FOREST, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, BiomeKeys.WINDSWEPT_SAVANNA, BiomeKeys.ERODED_BADLANDS, BiomeKeys.BAMBOO_JUNGLE, BiomeKeys.SOUL_SAND_VALLEY, BiomeKeys.CRIMSON_FOREST, BiomeKeys.WARPED_FOREST, BiomeKeys.BASALT_DELTAS, BiomeKeys.DRIPSTONE_CAVES, BiomeKeys.LUSH_CAVES, BiomeKeys.MEADOW, BiomeKeys.GROVE, BiomeKeys.SNOWY_SLOPES, BiomeKeys.FROZEN_PEAKS, BiomeKeys.JAGGED_PEAKS, BiomeKeys.STONY_PEAKS, BiomeKeys.MANGROVE_SWAMP, BiomeKeys.DEEP_DARK, BiomeKeys.CHERRY_GROVE);
    }

    static {
        var builtIn = BuiltinRegistries
                .createWrapperLookup()
                .createRegistryLookup()
                .getOptional(RegistryKeys.BIOME)
                .orElseThrow();

        var builtInBiomeRegistry = new SimpleRegistry<>(RegistryKeys.BIOME, Lifecycle.stable());

        BIOME_KEYS
                .forEach(
                        b -> builtInBiomeRegistry.add(b, builtIn.getOrThrow(b).value(), Lifecycle.stable())
                );

        BUILT_IN_BIOMES = builtInBiomeRegistry;
    }

    private SimpleBiomeRegistryUtils(){}

    public static int getBiomeRawId(RegistryKey<Biome> biomeRegistryKey) {

       Biome lookedUpBiome =BUILT_IN_BIOMES
               .createMutableEntryLookup()
               .getOrThrow(biomeRegistryKey)
               .value();

        return getBiomeRawId(lookedUpBiome);
    }

    private static int getBiomeRawId(Biome biome) {
        return ((SimpleRegistryAccessor<Biome>) SimpleBiomeRegistryUtils.BUILT_IN_BIOMES)
                .getEntryToRawId()
                .getOrDefault(biome, -1);
    }

}
