package vanadium.defaults;

import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.apache.commons.lang3.Range;
import vanadium.models.records.ColumnBounds;
import vanadium.utils.LoggerUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public final class DefaultColumns {
    private static final Map<Identifier, Identifier> dynamicColumns = new HashMap<>();

    private static final Map<Identifier, ColumnBounds> currentColumns = createCurrentColumnBoundaries();

    private static final Map<Identifier, ColumnBounds> legacyColumns = createLegacyColumnBoundaries();

    private static final Map<Identifier, ColumnBounds> stableColumns = createStableColumnBoundaries();
    private static final int TOTAL_VANILLA_BIOMES = new DynamicRegistryManager.ImmutableImpl(Registries.REGISTRIES.stream().toList()).get(RegistryKeys.BIOME).size();
    private static final int TOTAL_LEGACY_BIOMES = 176;

    private DefaultColumns(){

    }

    public static void reloadDefaultColumnBoundaries(DynamicRegistryManager manager) {
        dynamicColumns.clear();
        if(manager != null) {
            var biomeRegistry = manager.get(RegistryKeys.BIOME);

            biomeRegistry
                    .getEntrySet()
                    .stream()
                    .map(Map.Entry::getKey)
                    .filter(key -> !currentColumns.containsKey(key.getValue()))
                    .forEach(key -> dynamicColumns.put(key.getValue(), computeClosestDefaultBiomeByRegistry(key, biomeRegistry)));
        }
    }

    public static ColumnBounds getDefaultBoundaries(RegistryKey<Biome> biomeResourceLocation) {
        var currentBoundaries = currentColumns.get(biomeResourceLocation.getValue());

        if(currentBoundaries == null) {
            currentBoundaries = currentColumns.get(vanillaBiomeApproximation(biomeResourceLocation));

            if(currentBoundaries == null) {
                var msg = "Custom biomeSource has no approximate in vanilla: " + biomeResourceLocation.getValue();
                LoggerUtils.getLoggerInstance().warning(msg);
                throw new IllegalStateException(msg);
            }
        }
        return currentBoundaries;
    }

    public static ColumnBounds getOptifineBoundaries(RegistryKey<Biome> biomeResourceKey, Registry<Biome> biomeRegistry) {
        var currentBoundaries = currentColumns.get(biomeResourceKey.getValue());

        if(currentBoundaries == null) {
            int rawId = biomeRegistry.getRawId(biomeRegistry.get(biomeResourceKey));
            return new ColumnBounds(rawId, 1);
        }
        return currentBoundaries;
    }

    public static ColumnBounds getLegacyBoundaries(RegistryKey<Biome> biomeKey, Registry<Biome> biomeRegistry, boolean isUsingOptifine) {
       var bounds = legacyColumns.get(biomeKey.getValue());

       if(bounds == null
       && isUsingOptifine) {
           int rawId = biomeRegistry.getRawId(biomeRegistry.get(biomeKey));
           return new ColumnBounds(rawId - TOTAL_VANILLA_BIOMES + TOTAL_LEGACY_BIOMES, 1);
       }

       bounds = legacyColumns.get(vanillaBiomeApproximation(biomeKey));

       if(bounds == null) {
           var errorMessage = "Custom biome has no approximate to vanilla: " + biomeKey.getValue();
           LoggerUtils.getLoggerInstance().severe(errorMessage);
           throw  new IllegalStateException(errorMessage);
       }

       return bounds;
    }

    public static ColumnBounds getStableBoundaries(RegistryKey<Biome> biomeResourceKey) {
        var bounds = stableColumns.get(biomeResourceKey.getValue());

        if(bounds == null) {
            bounds = stableColumns.get(vanillaBiomeApproximation(biomeResourceKey));

            if(bounds == null) {
                var msg = "Custom biomeSource has no vanilla approximate biomeSource: " + biomeResourceKey;
                LoggerUtils.getLoggerInstance().severe(msg);
                throw new IllegalStateException(msg);
            }
        }
        return bounds;
    }

    private static Identifier vanillaBiomeApproximation(RegistryKey<Biome> biomeResourceLocation) {
        Identifier id;
        id = dynamicColumns.get(biomeResourceLocation.getValue());

        if(id != null) {
            return id;
        }

        var msg = "No column boundaries exist for the dynamic biomeSource: " + biomeResourceLocation;
        throw new IllegalArgumentException(msg);
    }

    private static Identifier computeClosestDefaultBiomeByRegistry(RegistryKey<Biome> biomeResourceKey, Registry<Biome> biomeRegistry) {
        var customBiome = biomeRegistry.get(biomeResourceKey);
        if(customBiome == null) {
            throw new IllegalStateException("Biome is not registered: " + biomeResourceKey.getValue());
        }

        double temperature = customBiome.getTemperature();
        double humidity = Range.between(0.0, 1.0).fit((double)customBiome.weather.downfall());
        double minimumDistanceSquare = Double.POSITIVE_INFINITY;

        Identifier minimumBiomeLocation = null;

        for(var entry : currentColumns.entrySet()) {
            var vanillaBiome = biomeRegistry.get(entry.getKey());

            if(vanillaBiome == null) {
                LoggerUtils.getLoggerInstance().severe("This vanilla biomeSource is not registered somehow: " + entry.getKey());
                continue;
            }

            var temperatureDelta = temperature - vanillaBiome.getTemperature();
            var humidityDelta =  humidity - Range.between(0.0, 1.0).fit((double)vanillaBiome.weather.downfall());
            var distance = Math.pow(temperatureDelta, 2) + Math.pow(humidityDelta, 2);

            if(distance < minimumDistanceSquare) {
                minimumDistanceSquare = distance;
                minimumBiomeLocation = entry.getKey();
            }
        }
        return minimumBiomeLocation;
    }

    private static Map<Identifier, ColumnBounds> createCurrentColumnBoundaries() {

        var biomeRegistry = new DynamicRegistryManager.ImmutableImpl(Registries.REGISTRIES.stream().toList())
                .get(RegistryKeys.BIOME);

        return biomeRegistry
                .stream()
                .map(biome -> entry(Objects.requireNonNull(biomeRegistry.getId(biome)), new ColumnBounds(biomeRegistry.getRawId(biome), 1)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    private static Map<Identifier, ColumnBounds> createLegacyColumnBoundaries() {
        return Map.<Identifier, ColumnBounds>ofEntries(
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
                // formerly "mutated" variants of biomes, normal biomeSource ID + 128, except for
                // the post-1.7 biomeSource additions.
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
        return Map.<Identifier, ColumnBounds>ofEntries(
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
