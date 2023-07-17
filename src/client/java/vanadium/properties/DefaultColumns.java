package vanadium.properties;

import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.apache.commons.lang3.Range;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vanadium.util.ColumnBounds;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public final class DefaultColumns {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Identifier, Identifier> dynamicColumns = new HashMap<>();
    private static final Map<Identifier, ColumnBounds> currentColumns = createCurrentColumnBoundaries();
    private static final Map<Identifier, ColumnBounds> legacyColumns = createLegacyColumnBoundaries();
    private static final Map<Identifier, ColumnBounds> stableColumns = createStableColumnBoundaries();

private DefaultColumns(){}

    public static ColumnBounds getDefaultBoundaries(RegistryKey<Biome> biomeKey) {
    var bounds = currentColumns.get(biomeKey.getValue());

    if(bounds != null){
        return bounds;
    }

        bounds = currentColumns.get(approximateToVanilla(biomeKey));

    if(bounds!= null){
        return bounds;
    }

    String message = "Custom biome has no approximate : " + biomeKey.getValue();
    throw new IllegalStateException(message);
}

public static ColumnBounds getOptifineBoundaries(RegistryKey<Biome> biomeKey, Registry<Biome> biomeRegistry) {
    var bounds = currentColumns.get(biomeKey.getValue());

    if(bounds!= null){
        return bounds;
    }

    int rawId = biomeRegistry.getRawId(biomeRegistry.get(biomeKey));
    return new ColumnBounds(rawId, 1);
}

public static ColumnBounds getLegacyBoundaries(RegistryKey<Biome> biomeKey, Registry<Biome> biomeRegistry, boolean isUsingOptifine) {
    var bounds = legacyColumns.get(biomeKey.getValue());

    if(bounds!= null){
        return bounds;
    }

    if(isUsingOptifine) {
        int rawId = biomeRegistry.getRawId(biomeRegistry.get(biomeKey));

        rawId = rawId - Registries.BIOME_SOURCE.size() - 176;

        return new ColumnBounds(rawId, 1);
    }

    bounds = legacyColumns.get(approximateToVanilla(biomeKey));

    if(bounds!= null){
        return bounds;
    }

    String message = "Custom biome has no legacy approximation : " + biomeKey.getValue();
    LOGGER.error(message);
    throw new IllegalStateException(message);
}

public static ColumnBounds getStableBoundaries(RegistryKey<Biome> biomeKey) {
    var bounds = stableColumns.get(biomeKey.getValue());

    if(bounds!= null){
        return bounds;
    }

    bounds = stableColumns.get(approximateToVanilla(biomeKey));

    if(bounds!= null){
        return bounds;
    }

    String message = "Custom biome has no stable approximation : " + biomeKey.getValue();
    throw new IllegalStateException(message);
}

private static Identifier computeClosestDefaultBiome(RegistryKey<Biome> biomeKey, Registry<Biome> biomeRegistry) {
    Biome customBiome = biomeRegistry.get(biomeKey);

    if(customBiome == null) {
        throw new IllegalArgumentException("Unknown biome: " + biomeKey.getValue());
    }

    double temperature = customBiome.getTemperature();
    double minimumDistanceSquared = Double.MAX_VALUE;
    double humidity = customBiome.weather.downfall();
    Identifier minimumBiomeIdentifier = null;

    for(var entry : currentColumns.entrySet()) {
        var vanillaBiomeKey = biomeRegistry.get(entry.getKey());

        if(vanillaBiomeKey == null) {
            LOGGER.error("The requested Vanilla Biome was not registered somehow : {} ", entry.getKey());
            continue;
        }

        double temperatureDelta = temperature - vanillaBiomeKey.getTemperature();
        double deltaHumidity = humidity - Range.between(0.0,1.0).fit((double)vanillaBiomeKey.weather.downfall());
        double deltaDistanceSquared = Math.pow(temperatureDelta, 2) + Math.pow(deltaHumidity, 2);

        if(deltaDistanceSquared < minimumDistanceSquared) {
            minimumDistanceSquared = deltaDistanceSquared;
            minimumBiomeIdentifier = entry.getKey();
        }
    }

    return minimumBiomeIdentifier;
}

private static Identifier approximateToVanilla(RegistryKey<Biome> biomeKey) {
    Identifier biomeId = dynamicColumns.get(biomeKey.getValue());

    if(biomeId != null) {
        return biomeId;
    }

    String message = "No column boundaries defined for dynamic biome: " + biomeKey.getValue();
    LOGGER.error(message);
    throw new IllegalArgumentException(message);
}

    private static Map<Identifier, ColumnBounds> createCurrentColumnBoundaries() {
    var map = new HashMap<Identifier, ColumnBounds>();
    Registries.BIOME_SOURCE.forEach(biome -> {
        Identifier id = Registries.BIOME_SOURCE.getId(biome);
        int rawId = Registries.BIOME_SOURCE.getRawId(biome);
        map.put(id, new ColumnBounds(rawId,1));
    });
    return map;
}

private static Map<Identifier, ColumnBounds> createLegacyColumnBoundaries() {
    return Map.ofEntries(
            entry(BiomeKeys.OCEAN.getValue(), new ColumnBounds(0, 1)),
    entry(BiomeKeys.PLAINS.getValue(), new ColumnBounds(1, 1)),
    entry(BiomeKeys.DESERT.getValue(), new ColumnBounds(2, 1)),
    entry(BiomeKeys.WINDSWEPT_HILLS.getValue(), new ColumnBounds(3, 1)),
    entry(BiomeKeys.FOREST.getValue(), new ColumnBounds(4, 1)),
    entry(BiomeKeys.TAIGA.getValue(), new ColumnBounds(5, 1)),
    entry(BiomeKeys.SWAMP.getValue(), new ColumnBounds(6, 1)),
    entry(BiomeKeys.RIVER.getValue(), new ColumnBounds(7, 1)),
    entry(BiomeKeys.NETHER_WASTES.getValue(), new ColumnBounds(8, 1)),
    entry(BiomeKeys.THE_END.getValue(), new ColumnBounds(9, 1)),
    entry(BiomeKeys.FROZEN_OCEAN.getValue(), new ColumnBounds(10, 1)),
    entry(BiomeKeys.FROZEN_RIVER.getValue(), new ColumnBounds(11, 1)),
    entry(BiomeKeys.SNOWY_PLAINS.getValue(), new ColumnBounds(12, 1)),
    entry(BiomeKeys.MUSHROOM_FIELDS.getValue(), new ColumnBounds(14, 1)),
    entry(BiomeKeys.BEACH.getValue(), new ColumnBounds(16, 1)),
    entry(BiomeKeys.JUNGLE.getValue(), new ColumnBounds(21, 1)),
    entry(BiomeKeys.SPARSE_JUNGLE.getValue(), new ColumnBounds(23, 1)),
    entry(BiomeKeys.DEEP_OCEAN.getValue(), new ColumnBounds(24, 1)),
    entry(BiomeKeys.STONY_SHORE.getValue(), new ColumnBounds(25, 1)),
    entry(BiomeKeys.SNOWY_BEACH.getValue(), new ColumnBounds(26, 1)),
    entry(BiomeKeys.BIRCH_FOREST.getValue(), new ColumnBounds(27, 1)),
    entry(BiomeKeys.DARK_FOREST.getValue(), new ColumnBounds(29, 1)),
    entry(BiomeKeys.SNOWY_TAIGA.getValue(), new ColumnBounds(30, 1)),
    entry(BiomeKeys.OLD_GROWTH_PINE_TAIGA.getValue(), new ColumnBounds(32, 1)),
    entry(BiomeKeys.WINDSWEPT_FOREST.getValue(), new ColumnBounds(34, 1)),
    entry(BiomeKeys.SAVANNA.getValue(), new ColumnBounds(35, 1)),
    entry(BiomeKeys.SAVANNA_PLATEAU.getValue(), new ColumnBounds(36, 1)),
    entry(BiomeKeys.BADLANDS.getValue(), new ColumnBounds(37, 1)),
    entry(BiomeKeys.WOODED_BADLANDS.getValue(), new ColumnBounds(38, 1)),
    entry(BiomeKeys.SMALL_END_ISLANDS.getValue(), new ColumnBounds(40, 1)),
    entry(BiomeKeys.END_MIDLANDS.getValue(), new ColumnBounds(41, 1)),
    entry(BiomeKeys.END_HIGHLANDS.getValue(), new ColumnBounds(42, 1)),
    entry(BiomeKeys.END_BARRENS.getValue(), new ColumnBounds(43, 1)),
    entry(BiomeKeys.WARM_OCEAN.getValue(), new ColumnBounds(44, 1)),
    entry(BiomeKeys.LUKEWARM_OCEAN.getValue(), new ColumnBounds(45, 1)),
    entry(BiomeKeys.COLD_OCEAN.getValue(), new ColumnBounds(46, 1)),
    entry(BiomeKeys.DEEP_LUKEWARM_OCEAN.getValue(), new ColumnBounds(48, 1)),
    entry(BiomeKeys.DEEP_COLD_OCEAN.getValue(), new ColumnBounds(49, 1)),
    entry(BiomeKeys.DEEP_FROZEN_OCEAN.getValue(), new ColumnBounds(50, 1)),
    // formerly "mutated" variants of biomes, normal biome ID + 128, except for
    // the post-1.7 biome additions.
    entry(BiomeKeys.THE_VOID.getValue(), new ColumnBounds(127, 1)),
    entry(BiomeKeys.SUNFLOWER_PLAINS.getValue(), new ColumnBounds(129, 1)),
    entry(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS.getValue(), new ColumnBounds(131, 1)),
    entry(BiomeKeys.FLOWER_FOREST.getValue(), new ColumnBounds(132, 1)),
    entry(BiomeKeys.ICE_SPIKES.getValue(), new ColumnBounds(140, 1)),
    entry(BiomeKeys.OLD_GROWTH_BIRCH_FOREST.getValue(), new ColumnBounds(155, 1)),
    entry(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA.getValue(), new ColumnBounds(160, 1)),
    entry(BiomeKeys.WINDSWEPT_SAVANNA.getValue(), new ColumnBounds(163, 1)),
    entry(BiomeKeys.ERODED_BADLANDS.getValue(), new ColumnBounds(165, 1)),
    entry(BiomeKeys.BAMBOO_JUNGLE.getValue(), new ColumnBounds(168, 1)),
    // 1.16 nether biomes
    entry(BiomeKeys.SOUL_SAND_VALLEY.getValue(), new ColumnBounds(170, 1)),
    entry(BiomeKeys.CRIMSON_FOREST.getValue(), new ColumnBounds(171, 1)),
    entry(BiomeKeys.WARPED_FOREST.getValue(), new ColumnBounds(172, 1)),
    entry(BiomeKeys.BASALT_DELTAS.getValue(), new ColumnBounds(173, 1)),
    // 1.17 cave biomes
    entry(BiomeKeys.DRIPSTONE_CAVES.getValue(), new ColumnBounds(174, 1)),
    entry(BiomeKeys.LUSH_CAVES.getValue(), new ColumnBounds(175, 1)),
    // 1.18 highland biomes
    // meadow -> plains
    entry(BiomeKeys.MEADOW.getValue(), new ColumnBounds(1, 1)),
    // grove -> snowy taiga
    entry(BiomeKeys.GROVE.getValue(), new ColumnBounds(30, 1)),
    // snowy slopes -> snowy plains
    entry(BiomeKeys.SNOWY_SLOPES.getValue(), new ColumnBounds(12, 1)),
    entry(BiomeKeys.FROZEN_PEAKS.getValue(), new ColumnBounds(12, 1)),
    // non-snow peaks -> windswept hills
    entry(BiomeKeys.JAGGED_PEAKS.getValue(), new ColumnBounds(3, 1)),
    entry(BiomeKeys.STONY_PEAKS.getValue(), new ColumnBounds(3, 1)),
    // 1.19 wild biomes
    // mangrove swamp -> swamp
    entry(BiomeKeys.MANGROVE_SWAMP.getValue(), new ColumnBounds(6, 1)),
    // deep dark -> lush caves
    entry(BiomeKeys.DEEP_DARK.getValue(), new ColumnBounds(175, 1)),
    // Cherry Grove -> Meadow
    entry(BiomeKeys.CHERRY_GROVE.getValue(), new ColumnBounds(1, 1))
    );
}

private static Map<Identifier, ColumnBounds> createStableColumnBoundaries() {
return Map.ofEntries(
    entry(BiomeKeys.THE_VOID.getValue(), new ColumnBounds(0, 1)),
    entry(BiomeKeys.PLAINS.getValue(), new ColumnBounds(1, 1)),
    entry(BiomeKeys.SUNFLOWER_PLAINS.getValue(), new ColumnBounds(2, 1)),
    entry(BiomeKeys.SNOWY_PLAINS.getValue(), new ColumnBounds(3, 1)),
    entry(BiomeKeys.ICE_SPIKES.getValue(), new ColumnBounds(4, 1)),
    entry(BiomeKeys.DESERT.getValue(), new ColumnBounds(5, 1)),
    entry(BiomeKeys.SWAMP.getValue(), new ColumnBounds(6, 1)),
    entry(BiomeKeys.FOREST.getValue(), new ColumnBounds(7, 1)),
    entry(BiomeKeys.FLOWER_FOREST.getValue(), new ColumnBounds(8, 1)),
    entry(BiomeKeys.BIRCH_FOREST.getValue(), new ColumnBounds(9, 1)),
    entry(BiomeKeys.DARK_FOREST.getValue(), new ColumnBounds(10, 1)),
    entry(BiomeKeys.OLD_GROWTH_BIRCH_FOREST.getValue(), new ColumnBounds(11, 1)),
    entry(BiomeKeys.OLD_GROWTH_PINE_TAIGA.getValue(), new ColumnBounds(12, 1)),
    entry(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA.getValue(), new ColumnBounds(13, 1)),
    entry(BiomeKeys.TAIGA.getValue(), new ColumnBounds(14, 1)),
    entry(BiomeKeys.SNOWY_TAIGA.getValue(), new ColumnBounds(15, 1)),
    entry(BiomeKeys.SAVANNA.getValue(), new ColumnBounds(16, 1)),
    entry(BiomeKeys.SAVANNA_PLATEAU.getValue(), new ColumnBounds(17, 1)),
    entry(BiomeKeys.WINDSWEPT_HILLS.getValue(), new ColumnBounds(18, 1)),
    entry(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS.getValue(), new ColumnBounds(19, 1)),
    entry(BiomeKeys.WINDSWEPT_FOREST.getValue(), new ColumnBounds(20, 1)),
    entry(BiomeKeys.WINDSWEPT_SAVANNA.getValue(), new ColumnBounds(21, 1)),
    entry(BiomeKeys.JUNGLE.getValue(), new ColumnBounds(22, 1)),
    entry(BiomeKeys.SPARSE_JUNGLE.getValue(), new ColumnBounds(23, 1)),
    entry(BiomeKeys.BAMBOO_JUNGLE.getValue(), new ColumnBounds(24, 1)),
    entry(BiomeKeys.BADLANDS.getValue(), new ColumnBounds(25, 1)),
    entry(BiomeKeys.ERODED_BADLANDS.getValue(), new ColumnBounds(26, 1)),
    entry(BiomeKeys.WOODED_BADLANDS.getValue(), new ColumnBounds(27, 1)),
    entry(BiomeKeys.MEADOW.getValue(), new ColumnBounds(28, 1)),
    entry(BiomeKeys.GROVE.getValue(), new ColumnBounds(29, 1)),
    entry(BiomeKeys.SNOWY_SLOPES.getValue(), new ColumnBounds(30, 1)),
    entry(BiomeKeys.FROZEN_PEAKS.getValue(), new ColumnBounds(31, 1)),
    entry(BiomeKeys.JAGGED_PEAKS.getValue(), new ColumnBounds(32, 1)),
    entry(BiomeKeys.STONY_PEAKS.getValue(), new ColumnBounds(33, 1)),
    entry(BiomeKeys.RIVER.getValue(), new ColumnBounds(34, 1)),
    entry(BiomeKeys.FROZEN_RIVER.getValue(), new ColumnBounds(35, 1)),
    entry(BiomeKeys.BEACH.getValue(), new ColumnBounds(36, 1)),
    entry(BiomeKeys.SNOWY_BEACH.getValue(), new ColumnBounds(37, 1)),
    entry(BiomeKeys.STONY_SHORE.getValue(), new ColumnBounds(38, 1)),
    entry(BiomeKeys.WARM_OCEAN.getValue(), new ColumnBounds(39, 1)),
    entry(BiomeKeys.LUKEWARM_OCEAN.getValue(), new ColumnBounds(40, 1)),
    entry(BiomeKeys.DEEP_LUKEWARM_OCEAN.getValue(), new ColumnBounds(41, 1)),
    entry(BiomeKeys.OCEAN.getValue(), new ColumnBounds(42, 1)),
    entry(BiomeKeys.DEEP_OCEAN.getValue(), new ColumnBounds(43, 1)),
    entry(BiomeKeys.COLD_OCEAN.getValue(), new ColumnBounds(44, 1)),
    entry(BiomeKeys.DEEP_COLD_OCEAN.getValue(), new ColumnBounds(45, 1)),
    entry(BiomeKeys.FROZEN_OCEAN.getValue(), new ColumnBounds(46, 1)),
    entry(BiomeKeys.DEEP_FROZEN_OCEAN.getValue(), new ColumnBounds(47, 1)),
    entry(BiomeKeys.MUSHROOM_FIELDS.getValue(), new ColumnBounds(48, 1)),
    entry(BiomeKeys.DRIPSTONE_CAVES.getValue(), new ColumnBounds(49, 1)),
    entry(BiomeKeys.LUSH_CAVES.getValue(), new ColumnBounds(50, 1)),
    entry(BiomeKeys.NETHER_WASTES.getValue(), new ColumnBounds(51, 1)),
    entry(BiomeKeys.WARPED_FOREST.getValue(), new ColumnBounds(52, 1)),
    entry(BiomeKeys.CRIMSON_FOREST.getValue(), new ColumnBounds(53, 1)),
    entry(BiomeKeys.SOUL_SAND_VALLEY.getValue(), new ColumnBounds(54, 1)),
    entry(BiomeKeys.BASALT_DELTAS.getValue(), new ColumnBounds(55, 1)),
    entry(BiomeKeys.THE_END.getValue(), new ColumnBounds(56, 1)),
    entry(BiomeKeys.END_HIGHLANDS.getValue(), new ColumnBounds(57, 1)),
    entry(BiomeKeys.END_MIDLANDS.getValue(), new ColumnBounds(58, 1)),
    entry(BiomeKeys.SMALL_END_ISLANDS.getValue(), new ColumnBounds(59, 1)),
    entry(BiomeKeys.END_BARRENS.getValue(), new ColumnBounds(60, 1)),
    // 1.19
    entry(BiomeKeys.MANGROVE_SWAMP.getValue(), new ColumnBounds(61, 1)),
    entry(BiomeKeys.DEEP_DARK.getValue(), new ColumnBounds(62, 1)),
    // 1.20
    entry(BiomeKeys.CHERRY_GROVE.getValue(), new ColumnBounds(63, 1))
);
}
}
