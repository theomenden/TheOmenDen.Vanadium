package vanadium.properties;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.apache.commons.lang3.Range;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vanadium.util.ColumnBounds;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public final class DefaultColumns {
    private static final Logger logger = LogManager.getLogger();

    private static final Map<ResourceLocation, ResourceLocation> dynamicColumns = new HashMap<>();

    private static final Map<ResourceLocation, ColumnBounds> currentColumns = createCurrentColumnBoundaries();

    private static final Map<ResourceLocation, ColumnBounds> legacyColumns = createLegacyColumnBoundaries();

    private static final Map<ResourceLocation, ColumnBounds> stableColumns = createStableColumnBoundaries();
    private static final int TOTAL_VANILLA_BIOMES = 63;
    private static final int TOTAL_LEGACY_BIOMES = 176;

    private DefaultColumns(){

    }

    public static void reloadDefaultColumnBoundaries(RegistryAccess manager) {
        dynamicColumns.clear();
        if(manager != null) {
            var biomeRegistry = manager.registry(Registries.BIOME).get();

            biomeRegistry
                    .entrySet()
                    .stream()
                    .map(Map.Entry::getKey)
                    .filter(key -> !currentColumns.containsKey(key.registry()))
                    .forEach(key -> dynamicColumns.put(key.registry(), computeBiomeClosestToDefault(key, biomeRegistry)));
        }
    }

    public static ColumnBounds getDefaultBoundaries(ResourceKey<Biome> biomeResourceLocation) {
        var currentBoundaries = currentColumns.get(biomeResourceLocation.location());

        if(currentBoundaries == null) {
            currentBoundaries = currentColumns.get(vanillaBiomeApproximation(biomeResourceLocation));

            if(currentBoundaries == null) {
                var msg = "Custom biomeSource has no approximate in vanilla: " + biomeResourceLocation.toString();
                logger.error(msg);
                throw new IllegalStateException(msg);
            }
        }
        return currentBoundaries;
    }

    public static ColumnBounds getOptifineBoundaries(ResourceKey<Biome> biomeResourceKey, ResourceKey<Biome> biomeRegistryKey) {
        var currentBoundaries = currentColumns.get(biomeResourceKey.location());

        if(currentBoundaries == null) {
            var targetBiome = BuiltInRegistries.BIOME_SOURCE.get(biomeRegistryKey.registry());
            var rawId = BuiltInRegistries.BIOME_SOURCE.getId(targetBiome);
            return new ColumnBounds(rawId, 1);
        }
        return currentBoundaries;
    }

    public static ColumnBounds getLegacyBoundaries(ResourceKey<Biome> biomeKey, ResourceKey<Biome> biomeRegistryKey, boolean isUsingOptifine) {
        var bounds = legacyColumns.get(biomeKey.location());

        if(bounds == null && isUsingOptifine) {
            var targetBiome = BuiltInRegistries.BIOME_SOURCE.get(biomeRegistryKey.registry());
            var rawId = BuiltInRegistries.BIOME_SOURCE.getId(targetBiome);
                return new ColumnBounds(rawId - TOTAL_VANILLA_BIOMES + TOTAL_LEGACY_BIOMES, 1);
            }

        bounds = legacyColumns.get(vanillaBiomeApproximation(biomeKey));
        if(bounds == null) {
            var msg = "The current biomeSource has no approximate vanilla biomeSource: " + biomeKey;
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
        return bounds;
    }

    public static ColumnBounds getStableBoundaries(ResourceKey<Biome> biomeResourceKey) {
        var bounds = stableColumns.get(biomeResourceKey.location());

        if(bounds == null) {
            bounds = stableColumns.get(vanillaBiomeApproximation(biomeResourceKey));

            if(bounds == null) {
                var msg = "Custom biomeSource has no vanilla approximate biomeSource: " + biomeResourceKey;
                logger.error(msg);
                throw new IllegalStateException(msg);
            }
        }
        return bounds;
    }

    private static ResourceLocation vanillaBiomeApproximation(ResourceKey<Biome> biomeResourceLocation) {
        ResourceLocation id;
        id = dynamicColumns.get(biomeResourceLocation.location());

        if(id != null) {
            return id;
        }

        var msg = "No column boundaries exist for the dynamic biomeSource: " + biomeResourceLocation;
        throw new IllegalArgumentException(msg);
    }

    private static ResourceLocation computeBiomeClosestToDefault(ResourceKey<Biome> biomeResourceKey, Registry<Biome> biomeRegistry) {
        var customBiome = biomeRegistry.get(biomeResourceKey);
        if(customBiome == null) {
            throw new IllegalStateException("Biome is not registered: " + biomeResourceKey.toString());
        }

        double temperature = customBiome.getBaseTemperature();
        double humidity = Range.between(0.0, 1.0).fit((double)customBiome.climateSettings.downfall());
        double minimumDistanceSquare = Double.POSITIVE_INFINITY;

        ResourceLocation minimumBiomeLocation = null;

        for(var entry : currentColumns.entrySet()) {
            var vanillaBiome = biomeRegistry.get(entry.getKey());

            if(vanillaBiome == null) {
                logger.error("This vanilla biomeSource is not registered somehow: {}", entry.getKey());
                continue;
            }

            var temperatureDelta = temperature - vanillaBiome.getBaseTemperature();
            var humidityDelta =  humidity - Range.between(0.0, 1.0).fit((double)vanillaBiome.climateSettings.downfall());
            var distance = Math.pow(temperatureDelta, 2) + Math.pow(humidityDelta, 2);

            if(distance < minimumDistanceSquare) {
                minimumDistanceSquare = distance;
                minimumBiomeLocation = entry.getKey();
            }
        }
        return minimumBiomeLocation;
    }

    private static Map<ResourceLocation, ColumnBounds> createCurrentColumnBoundaries() {
        var map = new HashMap<ResourceLocation, ColumnBounds>();

        for(var biomeSource : BuiltInRegistries.BIOME_SOURCE) {

            var resourceKey = BuiltInRegistries.BIOME_SOURCE.key();
            var id = BuiltInRegistries.BIOME_SOURCE.getId(biomeSource);

            map.put(resourceKey.registry(), new ColumnBounds(id, 1));
        }
        return map;
    }

    private static Map<ResourceLocation, ColumnBounds> createLegacyColumnBoundaries() {
        return Map.ofEntries(
                entry(Biomes.OCEAN.location(), new ColumnBounds(0, 1)),
                entry(Biomes.PLAINS.location(), new ColumnBounds(1, 1)),
                entry(Biomes.DESERT.location(), new ColumnBounds(2, 1)),
                entry(Biomes.WINDSWEPT_HILLS.location(), new ColumnBounds(3, 1)),
                entry(Biomes.FOREST.location(), new ColumnBounds(4, 1)),
                entry(Biomes.TAIGA.location(), new ColumnBounds(5, 1)),
                entry(Biomes.SWAMP.location(), new ColumnBounds(6, 1)),
                entry(Biomes.RIVER.location(), new ColumnBounds(7, 1)),
                entry(Biomes.NETHER_WASTES.location(), new ColumnBounds(8, 1)),
                entry(Biomes.THE_END.location(), new ColumnBounds(9, 1)),
                entry(Biomes.FROZEN_OCEAN.location(), new ColumnBounds(10, 1)),
                entry(Biomes.FROZEN_RIVER.location(), new ColumnBounds(11, 1)),
                entry(Biomes.SNOWY_PLAINS.location(), new ColumnBounds(12, 1)),
                entry(Biomes.MUSHROOM_FIELDS.location(), new ColumnBounds(14, 1)),
                entry(Biomes.BEACH.location(), new ColumnBounds(16, 1)),
                entry(Biomes.JUNGLE.location(), new ColumnBounds(21, 1)),
                entry(Biomes.SPARSE_JUNGLE.location(), new ColumnBounds(23, 1)),
                entry(Biomes.DEEP_OCEAN.location(), new ColumnBounds(24, 1)),
                entry(Biomes.STONY_SHORE.location(), new ColumnBounds(25, 1)),
                entry(Biomes.SNOWY_BEACH.location(), new ColumnBounds(26, 1)),
                entry(Biomes.BIRCH_FOREST.location(), new ColumnBounds(27, 1)),
                entry(Biomes.DARK_FOREST.location(), new ColumnBounds(29, 1)),
                entry(Biomes.SNOWY_TAIGA.location(), new ColumnBounds(30, 1)),
                entry(Biomes.OLD_GROWTH_PINE_TAIGA.location(), new ColumnBounds(32, 1)),
                entry(Biomes.WINDSWEPT_FOREST.location(), new ColumnBounds(34, 1)),
                entry(Biomes.SAVANNA.location(), new ColumnBounds(35, 1)),
                entry(Biomes.SAVANNA_PLATEAU.location(), new ColumnBounds(36, 1)),
                entry(Biomes.BADLANDS.location(), new ColumnBounds(37, 1)),
                entry(Biomes.WOODED_BADLANDS.location(), new ColumnBounds(38, 1)),
                entry(Biomes.SMALL_END_ISLANDS.location(), new ColumnBounds(40, 1)),
                entry(Biomes.END_MIDLANDS.location(), new ColumnBounds(41, 1)),
                entry(Biomes.END_HIGHLANDS.location(), new ColumnBounds(42, 1)),
                entry(Biomes.END_BARRENS.location(), new ColumnBounds(43, 1)),
                entry(Biomes.WARM_OCEAN.location(), new ColumnBounds(44, 1)),
                entry(Biomes.LUKEWARM_OCEAN.location(), new ColumnBounds(45, 1)),
                entry(Biomes.COLD_OCEAN.location(), new ColumnBounds(46, 1)),
                entry(Biomes.DEEP_LUKEWARM_OCEAN.location(), new ColumnBounds(48, 1)),
                entry(Biomes.DEEP_COLD_OCEAN.location(), new ColumnBounds(49, 1)),
                entry(Biomes.DEEP_FROZEN_OCEAN.location(), new ColumnBounds(50, 1)),
                // formerly "mutated" variants of biomes, normal biomeSource ID + 128, except for
                // the post-1.7 biomeSource additions.
                entry(Biomes.THE_VOID.location(), new ColumnBounds(127, 1)),
                entry(Biomes.SUNFLOWER_PLAINS.location(), new ColumnBounds(129, 1)),
                entry(Biomes.WINDSWEPT_GRAVELLY_HILLS.location(), new ColumnBounds(131, 1)),
                entry(Biomes.FLOWER_FOREST.location(), new ColumnBounds(132, 1)),
                entry(Biomes.ICE_SPIKES.location(), new ColumnBounds(140, 1)),
                entry(Biomes.OLD_GROWTH_BIRCH_FOREST.location(), new ColumnBounds(155, 1)),
                entry(Biomes.OLD_GROWTH_SPRUCE_TAIGA.location(), new ColumnBounds(160, 1)),
                entry(Biomes.WINDSWEPT_SAVANNA.location(), new ColumnBounds(163, 1)),
                entry(Biomes.ERODED_BADLANDS.location(), new ColumnBounds(165, 1)),
                entry(Biomes.BAMBOO_JUNGLE.location(), new ColumnBounds(168, 1)),
                // 1.16 nether biomes
                entry(Biomes.SOUL_SAND_VALLEY.location(), new ColumnBounds(170, 1)),
                entry(Biomes.CRIMSON_FOREST.location(), new ColumnBounds(171, 1)),
                entry(Biomes.WARPED_FOREST.location(), new ColumnBounds(172, 1)),
                entry(Biomes.BASALT_DELTAS.location(), new ColumnBounds(173, 1)),
                // 1.17 cave biomes
                entry(Biomes.DRIPSTONE_CAVES.location(), new ColumnBounds(174, 1)),
                entry(Biomes.LUSH_CAVES.location(), new ColumnBounds(175, 1)),
                // 1.18 highland biomes
                // meadow -> plains
                entry(Biomes.MEADOW.location(), new ColumnBounds(1, 1)),
                // grove -> snowy taiga
                entry(Biomes.GROVE.location(), new ColumnBounds(30, 1)),
                // snowy slopes -> snowy plains
                entry(Biomes.SNOWY_SLOPES.location(), new ColumnBounds(12, 1)),
                entry(Biomes.FROZEN_PEAKS.location(), new ColumnBounds(12, 1)),
                // non-snow peaks -> windswept hills
                entry(Biomes.JAGGED_PEAKS.location(), new ColumnBounds(3, 1)),
                entry(Biomes.STONY_PEAKS.location(), new ColumnBounds(3, 1)),
                // 1.19 wild biomes
                // mangrove swamp -> swamp
                entry(Biomes.MANGROVE_SWAMP.location(), new ColumnBounds(6, 1)),
                // deep dark -> lush caves
                entry(Biomes.DEEP_DARK.location(), new ColumnBounds(175, 1)),
                // Cherry Grove -> Meadow
                entry(Biomes.CHERRY_GROVE.location(), new ColumnBounds(1, 1))
        );
    }

    private static Map<ResourceLocation, ColumnBounds> createStableColumnBoundaries() {
        return Map.ofEntries(
                entry(Biomes.THE_VOID.location(), new ColumnBounds(0, 1)),
                entry(Biomes.PLAINS.location(), new ColumnBounds(1, 1)),
                entry(Biomes.SUNFLOWER_PLAINS.location(), new ColumnBounds(2, 1)),
                entry(Biomes.SNOWY_PLAINS.location(), new ColumnBounds(3, 1)),
                entry(Biomes.ICE_SPIKES.location(), new ColumnBounds(4, 1)),
                entry(Biomes.DESERT.location(), new ColumnBounds(5, 1)),
                entry(Biomes.SWAMP.location(), new ColumnBounds(6, 1)),
                entry(Biomes.FOREST.location(), new ColumnBounds(7, 1)),
                entry(Biomes.FLOWER_FOREST.location(), new ColumnBounds(8, 1)),
                entry(Biomes.BIRCH_FOREST.location(), new ColumnBounds(9, 1)),
                entry(Biomes.DARK_FOREST.location(), new ColumnBounds(10, 1)),
                entry(Biomes.OLD_GROWTH_BIRCH_FOREST.location(), new ColumnBounds(11, 1)),
                entry(Biomes.OLD_GROWTH_PINE_TAIGA.location(), new ColumnBounds(12, 1)),
                entry(Biomes.OLD_GROWTH_SPRUCE_TAIGA.location(), new ColumnBounds(13, 1)),
                entry(Biomes.TAIGA.location(), new ColumnBounds(14, 1)),
                entry(Biomes.SNOWY_TAIGA.location(), new ColumnBounds(15, 1)),
                entry(Biomes.SAVANNA.location(), new ColumnBounds(16, 1)),
                entry(Biomes.SAVANNA_PLATEAU.location(), new ColumnBounds(17, 1)),
                entry(Biomes.WINDSWEPT_HILLS.location(), new ColumnBounds(18, 1)),
                entry(Biomes.WINDSWEPT_GRAVELLY_HILLS.location(), new ColumnBounds(19, 1)),
                entry(Biomes.WINDSWEPT_FOREST.location(), new ColumnBounds(20, 1)),
                entry(Biomes.WINDSWEPT_SAVANNA.location(), new ColumnBounds(21, 1)),
                entry(Biomes.JUNGLE.location(), new ColumnBounds(22, 1)),
                entry(Biomes.SPARSE_JUNGLE.location(), new ColumnBounds(23, 1)),
                entry(Biomes.BAMBOO_JUNGLE.location(), new ColumnBounds(24, 1)),
                entry(Biomes.BADLANDS.location(), new ColumnBounds(25, 1)),
                entry(Biomes.ERODED_BADLANDS.location(), new ColumnBounds(26, 1)),
                entry(Biomes.WOODED_BADLANDS.location(), new ColumnBounds(27, 1)),
                entry(Biomes.MEADOW.location(), new ColumnBounds(28, 1)),
                entry(Biomes.GROVE.location(), new ColumnBounds(29, 1)),
                entry(Biomes.SNOWY_SLOPES.location(), new ColumnBounds(30, 1)),
                entry(Biomes.FROZEN_PEAKS.location(), new ColumnBounds(31, 1)),
                entry(Biomes.JAGGED_PEAKS.location(), new ColumnBounds(32, 1)),
                entry(Biomes.STONY_PEAKS.location(), new ColumnBounds(33, 1)),
                entry(Biomes.RIVER.location(), new ColumnBounds(34, 1)),
                entry(Biomes.FROZEN_RIVER.location(), new ColumnBounds(35, 1)),
                entry(Biomes.BEACH.location(), new ColumnBounds(36, 1)),
                entry(Biomes.SNOWY_BEACH.location(), new ColumnBounds(37, 1)),
                entry(Biomes.STONY_SHORE.location(), new ColumnBounds(38, 1)),
                entry(Biomes.WARM_OCEAN.location(), new ColumnBounds(39, 1)),
                entry(Biomes.LUKEWARM_OCEAN.location(), new ColumnBounds(40, 1)),
                entry(Biomes.DEEP_LUKEWARM_OCEAN.location(), new ColumnBounds(41, 1)),
                entry(Biomes.OCEAN.location(), new ColumnBounds(42, 1)),
                entry(Biomes.DEEP_OCEAN.location(), new ColumnBounds(43, 1)),
                entry(Biomes.COLD_OCEAN.location(), new ColumnBounds(44, 1)),
                entry(Biomes.DEEP_COLD_OCEAN.location(), new ColumnBounds(45, 1)),
                entry(Biomes.FROZEN_OCEAN.location(), new ColumnBounds(46, 1)),
                entry(Biomes.DEEP_FROZEN_OCEAN.location(), new ColumnBounds(47, 1)),
                entry(Biomes.MUSHROOM_FIELDS.location(), new ColumnBounds(48, 1)),
                entry(Biomes.DRIPSTONE_CAVES.location(), new ColumnBounds(49, 1)),
                entry(Biomes.LUSH_CAVES.location(), new ColumnBounds(50, 1)),
                entry(Biomes.NETHER_WASTES.location(), new ColumnBounds(51, 1)),
                entry(Biomes.WARPED_FOREST.location(), new ColumnBounds(52, 1)),
                entry(Biomes.CRIMSON_FOREST.location(), new ColumnBounds(53, 1)),
                entry(Biomes.SOUL_SAND_VALLEY.location(), new ColumnBounds(54, 1)),
                entry(Biomes.BASALT_DELTAS.location(), new ColumnBounds(55, 1)),
                entry(Biomes.THE_END.location(), new ColumnBounds(56, 1)),
                entry(Biomes.END_HIGHLANDS.location(), new ColumnBounds(57, 1)),
                entry(Biomes.END_MIDLANDS.location(), new ColumnBounds(58, 1)),
                entry(Biomes.SMALL_END_ISLANDS.location(), new ColumnBounds(59, 1)),
                entry(Biomes.END_BARRENS.location(), new ColumnBounds(60, 1)),
                // 1.19
                entry(Biomes.MANGROVE_SWAMP.location(), new ColumnBounds(61, 1)),
                entry(Biomes.DEEP_DARK.location(), new ColumnBounds(62, 1)),
                // 1.20
                entry(Biomes.CHERRY_GROVE.location(), new ColumnBounds(63, 1))
        );
    }
}
