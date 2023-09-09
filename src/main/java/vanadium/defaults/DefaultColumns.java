package vanadium.defaults;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.apache.commons.lang3.Range;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vanadium.models.records.ColumnBounds;
import vanadium.utils.SimpleBiomeRegistryUtils;

import java.util.HashMap;
import java.util.Map;

public final class DefaultColumns {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceLocation, ResourceLocation> dynamicColumns = new HashMap<>();
    private static final Map<ResourceLocation, ColumnBounds> currentColumns = createCurrentColumnBoundaries();
    private static final Map<ResourceLocation, ColumnBounds> legacyColumns = createLegacyColumnBoundaries();
    private static final Map<ResourceLocation, ColumnBounds> stableColumns = createStableColumnBoundaries();
    private static final int TOTAL_VANILLA_BIOMES = SimpleBiomeRegistryUtils.getBIOME_KEYS().size();
    private static final int TOTAL_LEGACY_BIOMES = 176;

    private DefaultColumns(){}
    public static void reloadDefaultColumnBoundaries(RegistryAccess manager) {
        dynamicColumns.clear();
        if(manager != null) {
            var biomeRegistry = manager.registry(Registries.BIOME).get();

            biomeRegistry
                    .entrySet()
                    .stream()
                    .map(Map.Entry::getKey)
                    .filter(key -> !currentColumns.containsKey(key.location()))
                    .forEach(key -> dynamicColumns.put(key.location(), computeClosestDefaultBiomeByRegistry(key, biomeRegistry)));
        }
    }
    public static ColumnBounds getDefaultBoundaries(ResourceKey<Biome> biomeResourceLocation) {
        var currentBoundaries = currentColumns.get(biomeResourceLocation.location());

        if(currentBoundaries == null) {
            currentBoundaries = currentColumns.get(vanillaBiomeApproximation(biomeResourceLocation));

            if(currentBoundaries == null) {
                var msg = "Custom biome has no approximate in vanilla: " + biomeResourceLocation.location();
                LOGGER.warn(msg);
                throw new IllegalStateException(msg);
            }
        }
        return currentBoundaries;
    }
    public static ColumnBounds getOptifineBoundaries(ResourceKey<Biome> biomeResourceKey, ResourceKey<Registry<Biome>> biomeRegistry) {
        var currentBoundaries = currentColumns.get(biomeResourceKey.location());

        if(currentBoundaries == null) {
            var biomeReference = VanillaRegistries.createLookup()
                                                  .asGetterLookup()
                                                  .lookupOrThrow(biomeRegistry)
                                                  .getOrThrow(biomeResourceKey);

            int rawId = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
                                      .registryOrThrow(biomeRegistry)
                                      .getId(biomeReference.value());

            return new ColumnBounds(rawId, 1);
        }
        return currentBoundaries;
    }
    public static ColumnBounds getLegacyBoundaries(ResourceKey<Biome> biomeKey, ResourceKey<Registry<Biome>> biomeRegistry, boolean isUsingOptifine) {
        var bounds = legacyColumns.get(biomeKey.location());

        if(bounds == null
                && isUsingOptifine) {
            var biomeReference = VanillaRegistries.createLookup()
                                                  .asGetterLookup()
                                                  .lookupOrThrow(biomeRegistry)
                                                  .getOrThrow(biomeKey);

            int rawId = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
                                      .registryOrThrow(biomeRegistry)
                                      .getId(biomeReference.value());

            return new ColumnBounds(rawId - TOTAL_VANILLA_BIOMES + TOTAL_LEGACY_BIOMES, 1);
        }

        bounds = legacyColumns.get(vanillaBiomeApproximation(biomeKey));

        if(bounds == null) {
            var errorMessage = "Custom biome has no approximate to vanilla: " + biomeKey.location();
            LOGGER.error(errorMessage);
            throw  new IllegalStateException(errorMessage);
        }

        return bounds;
    }
    public static ColumnBounds getStableBoundaries(ResourceKey<Biome> biomeResourceKey) {
        var bounds = stableColumns.get(biomeResourceKey.location());

        if(bounds == null) {
            bounds = stableColumns.get(vanillaBiomeApproximation(biomeResourceKey));

            if(bounds == null) {
                var msg = "Custom biomeSource has no vanilla approximate biome Source: " + biomeResourceKey;
                LOGGER.error(msg);
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
    private static ResourceLocation computeClosestDefaultBiomeByRegistry(ResourceKey<Biome> biomeResourceKey, Registry<Biome> biomeRegistry) {
        var customBiome = biomeRegistry.get(biomeResourceKey);
        if(customBiome == null) {
            throw new IllegalStateException("Biome is not registered: " + biomeResourceKey.location());
        }

        double temperature = customBiome.climateSettings.temperature();
        double humidity = Range.between(0.0, 1.0).fit((double)customBiome.climateSettings.downfall());
        double minimumDistanceSquare = Double.POSITIVE_INFINITY;

        ResourceLocation minimumBiomeLocation = null;

        for(var entry : currentColumns.entrySet()) {
            var vanillaBiome = biomeRegistry.get(entry.getKey());

            if(vanillaBiome == null) {
                LOGGER.error("This vanilla biome is not registered somehow: " + entry.getKey());
                continue;
            }

            var temperatureDelta = temperature - vanillaBiome.climateSettings.temperature();
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
      Map<ResourceLocation, ColumnBounds> result = new HashMap<>();

      for(var biomeRegistryKey : SimpleBiomeRegistryUtils.getBIOME_KEYS()) {
         result.put(biomeRegistryKey.location(), new ColumnBounds(SimpleBiomeRegistryUtils.getBiomeRawId(biomeRegistryKey), 1) );
        }

      return result;
    }
    private static Map<ResourceLocation, ColumnBounds> createLegacyColumnBoundaries() {
        return Map.<ResourceLocation, ColumnBounds>ofEntries(
                Map.entry(Biomes.OCEAN.location(), new ColumnBounds(0, 1)),
                Map.entry(Biomes.PLAINS.location(), new ColumnBounds(1, 1)),
                Map.entry(Biomes.DESERT.location(), new ColumnBounds(2, 1)),
                Map.entry(Biomes.WINDSWEPT_HILLS.location(), new ColumnBounds(3, 1)),
                Map.entry(Biomes.FOREST.location(), new ColumnBounds(4, 1)),
                Map.entry(Biomes.TAIGA.location(), new ColumnBounds(5, 1)),
                Map.entry(Biomes.SWAMP.location(), new ColumnBounds(6, 1)),
                Map.entry(Biomes.RIVER.location(), new ColumnBounds(7, 1)),
                Map.entry(Biomes.NETHER_WASTES.location(), new ColumnBounds(8, 1)),
                Map.entry(Biomes.THE_END.location(), new ColumnBounds(9, 1)),
                Map.entry(Biomes.FROZEN_OCEAN.location(), new ColumnBounds(10, 1)),
                Map.entry(Biomes.FROZEN_RIVER.location(), new ColumnBounds(11, 1)),
                Map.entry(Biomes.SNOWY_PLAINS.location(), new ColumnBounds(12, 1)),
                Map.entry(Biomes.MUSHROOM_FIELDS.location(), new ColumnBounds(14, 1)),
                Map.entry(Biomes.BEACH.location(), new ColumnBounds(16, 1)),
                Map.entry(Biomes.JUNGLE.location(), new ColumnBounds(21, 1)),
                Map.entry(Biomes.SPARSE_JUNGLE.location(), new ColumnBounds(23, 1)),
                Map.entry(Biomes.DEEP_OCEAN.location(), new ColumnBounds(24, 1)),
                Map.entry(Biomes.STONY_SHORE.location(), new ColumnBounds(25, 1)),
                Map.entry(Biomes.SNOWY_BEACH.location(), new ColumnBounds(26, 1)),
                Map.entry(Biomes.BIRCH_FOREST.location(), new ColumnBounds(27, 1)),
                Map.entry(Biomes.DARK_FOREST.location(), new ColumnBounds(29, 1)),
                Map.entry(Biomes.SNOWY_TAIGA.location(), new ColumnBounds(30, 1)),
                Map.entry(Biomes.OLD_GROWTH_PINE_TAIGA.location(), new ColumnBounds(32, 1)),
                Map.entry(Biomes.WINDSWEPT_FOREST.location(), new ColumnBounds(34, 1)),
                Map.entry(Biomes.SAVANNA.location(), new ColumnBounds(35, 1)),
                Map.entry(Biomes.SAVANNA_PLATEAU.location(), new ColumnBounds(36, 1)),
                Map.entry(Biomes.BADLANDS.location(), new ColumnBounds(37, 1)),
                Map.entry(Biomes.WOODED_BADLANDS.location(), new ColumnBounds(38, 1)),
                Map.entry(Biomes.SMALL_END_ISLANDS.location(), new ColumnBounds(40, 1)),
                Map.entry(Biomes.END_MIDLANDS.location(), new ColumnBounds(41, 1)),
                Map.entry(Biomes.END_HIGHLANDS.location(), new ColumnBounds(42, 1)),
                Map.entry(Biomes.END_BARRENS.location(), new ColumnBounds(43, 1)),
                Map.entry(Biomes.WARM_OCEAN.location(), new ColumnBounds(44, 1)),
                Map.entry(Biomes.LUKEWARM_OCEAN.location(), new ColumnBounds(45, 1)),
                Map.entry(Biomes.COLD_OCEAN.location(), new ColumnBounds(46, 1)),
                Map.entry(Biomes.DEEP_LUKEWARM_OCEAN.location(), new ColumnBounds(48, 1)),
                Map.entry(Biomes.DEEP_COLD_OCEAN.location(), new ColumnBounds(49, 1)),
                Map.entry(Biomes.DEEP_FROZEN_OCEAN.location(), new ColumnBounds(50, 1)),
                // formerly "mutated" variants of biomes, normal biomeSource ID + 128, except for
                // the post-1.7 biomeSource additions.
                Map.entry(Biomes.THE_VOID.location(), new ColumnBounds(127, 1)),
                Map.entry(Biomes.SUNFLOWER_PLAINS.location(), new ColumnBounds(129, 1)),
                Map.entry(Biomes.WINDSWEPT_GRAVELLY_HILLS.location(), new ColumnBounds(131, 1)),
                Map.entry(Biomes.FLOWER_FOREST.location(), new ColumnBounds(132, 1)),
                Map.entry(Biomes.ICE_SPIKES.location(), new ColumnBounds(140, 1)),
                Map.entry(Biomes.OLD_GROWTH_BIRCH_FOREST.location(), new ColumnBounds(155, 1)),
                Map.entry(Biomes.OLD_GROWTH_SPRUCE_TAIGA.location(), new ColumnBounds(160, 1)),
                Map.entry(Biomes.WINDSWEPT_SAVANNA.location(), new ColumnBounds(163, 1)),
                Map.entry(Biomes.ERODED_BADLANDS.location(), new ColumnBounds(165, 1)),
                Map.entry(Biomes.BAMBOO_JUNGLE.location(), new ColumnBounds(168, 1)),
                // 1.16 nether biomes
                Map.entry(Biomes.SOUL_SAND_VALLEY.location(), new ColumnBounds(170, 1)),
                Map.entry(Biomes.CRIMSON_FOREST.location(), new ColumnBounds(171, 1)),
                Map.entry(Biomes.WARPED_FOREST.location(), new ColumnBounds(172, 1)),
                Map.entry(Biomes.BASALT_DELTAS.location(), new ColumnBounds(173, 1)),
                // 1.17 cave biomes
                Map.entry(Biomes.DRIPSTONE_CAVES.location(), new ColumnBounds(174, 1)),
                Map.entry(Biomes.LUSH_CAVES.location(), new ColumnBounds(175, 1)),
                // 1.18 highland biomes
                // meadow -> plains
                Map.entry(Biomes.MEADOW.location(), new ColumnBounds(1, 1)),
                // grove -> snowy taiga
                Map.entry(Biomes.GROVE.location(), new ColumnBounds(30, 1)),
                // snowy slopes -> snowy plains
                Map.entry(Biomes.SNOWY_SLOPES.location(), new ColumnBounds(12, 1)),
                Map.entry(Biomes.FROZEN_PEAKS.location(), new ColumnBounds(12, 1)),
                // non-snow peaks -> windswept hills
                Map.entry(Biomes.JAGGED_PEAKS.location(), new ColumnBounds(3, 1)),
                Map.entry(Biomes.STONY_PEAKS.location(), new ColumnBounds(3, 1)),
                // 1.19 wild biomes
                // mangrove swamp -> swamp
                Map.entry(Biomes.MANGROVE_SWAMP.location(), new ColumnBounds(6, 1)),
                // deep dark -> lush caves
                Map.entry(Biomes.DEEP_DARK.location(), new ColumnBounds(175, 1)),
                // Cherry Grove -> Meadow
                Map.entry(Biomes.CHERRY_GROVE.location(), new ColumnBounds(1, 1))
        );
    }
    private static Map<ResourceLocation, ColumnBounds> createStableColumnBoundaries() {
        return Map.<ResourceLocation, ColumnBounds>ofEntries(
                Map.entry(Biomes.THE_VOID.location(), new ColumnBounds(0, 1)),
                Map.entry(Biomes.PLAINS.location(), new ColumnBounds(1, 1)),
                Map.entry(Biomes.SUNFLOWER_PLAINS.location(), new ColumnBounds(2, 1)),
                Map.entry(Biomes.SNOWY_PLAINS.location(), new ColumnBounds(3, 1)),
                Map.entry(Biomes.ICE_SPIKES.location(), new ColumnBounds(4, 1)),
                Map.entry(Biomes.DESERT.location(), new ColumnBounds(5, 1)),
                Map.entry(Biomes.SWAMP.location(), new ColumnBounds(6, 1)),
                Map.entry(Biomes.FOREST.location(), new ColumnBounds(7, 1)),
                Map.entry(Biomes.FLOWER_FOREST.location(), new ColumnBounds(8, 1)),
                Map.entry(Biomes.BIRCH_FOREST.location(), new ColumnBounds(9, 1)),
                Map.entry(Biomes.DARK_FOREST.location(), new ColumnBounds(10, 1)),
                Map.entry(Biomes.OLD_GROWTH_BIRCH_FOREST.location(), new ColumnBounds(11, 1)),
                Map.entry(Biomes.OLD_GROWTH_PINE_TAIGA.location(), new ColumnBounds(12, 1)),
                Map.entry(Biomes.OLD_GROWTH_SPRUCE_TAIGA.location(), new ColumnBounds(13, 1)),
                Map.entry(Biomes.TAIGA.location(), new ColumnBounds(14, 1)),
                Map.entry(Biomes.SNOWY_TAIGA.location(), new ColumnBounds(15, 1)),
                Map.entry(Biomes.SAVANNA.location(), new ColumnBounds(16, 1)),
                Map.entry(Biomes.SAVANNA_PLATEAU.location(), new ColumnBounds(17, 1)),
                Map.entry(Biomes.WINDSWEPT_HILLS.location(), new ColumnBounds(18, 1)),
                Map.entry(Biomes.WINDSWEPT_GRAVELLY_HILLS.location(), new ColumnBounds(19, 1)),
                Map.entry(Biomes.WINDSWEPT_FOREST.location(), new ColumnBounds(20, 1)),
                Map.entry(Biomes.WINDSWEPT_SAVANNA.location(), new ColumnBounds(21, 1)),
                Map.entry(Biomes.JUNGLE.location(), new ColumnBounds(22, 1)),
                Map.entry(Biomes.SPARSE_JUNGLE.location(), new ColumnBounds(23, 1)),
                Map.entry(Biomes.BAMBOO_JUNGLE.location(), new ColumnBounds(24, 1)),
                Map.entry(Biomes.BADLANDS.location(), new ColumnBounds(25, 1)),
                Map.entry(Biomes.ERODED_BADLANDS.location(), new ColumnBounds(26, 1)),
                Map.entry(Biomes.WOODED_BADLANDS.location(), new ColumnBounds(27, 1)),
                Map.entry(Biomes.MEADOW.location(), new ColumnBounds(28, 1)),
                Map.entry(Biomes.GROVE.location(), new ColumnBounds(29, 1)),
                Map.entry(Biomes.SNOWY_SLOPES.location(), new ColumnBounds(30, 1)),
                Map.entry(Biomes.FROZEN_PEAKS.location(), new ColumnBounds(31, 1)),
                Map.entry(Biomes.JAGGED_PEAKS.location(), new ColumnBounds(32, 1)),
                Map.entry(Biomes.STONY_PEAKS.location(), new ColumnBounds(33, 1)),
                Map.entry(Biomes.RIVER.location(), new ColumnBounds(34, 1)),
                Map.entry(Biomes.FROZEN_RIVER.location(), new ColumnBounds(35, 1)),
                Map.entry(Biomes.BEACH.location(), new ColumnBounds(36, 1)),
                Map.entry(Biomes.SNOWY_BEACH.location(), new ColumnBounds(37, 1)),
                Map.entry(Biomes.STONY_SHORE.location(), new ColumnBounds(38, 1)),
                Map.entry(Biomes.WARM_OCEAN.location(), new ColumnBounds(39, 1)),
                Map.entry(Biomes.LUKEWARM_OCEAN.location(), new ColumnBounds(40, 1)),
                Map.entry(Biomes.DEEP_LUKEWARM_OCEAN.location(), new ColumnBounds(41, 1)),
                Map.entry(Biomes.OCEAN.location(), new ColumnBounds(42, 1)),
                Map.entry(Biomes.DEEP_OCEAN.location(), new ColumnBounds(43, 1)),
                Map.entry(Biomes.COLD_OCEAN.location(), new ColumnBounds(44, 1)),
                Map.entry(Biomes.DEEP_COLD_OCEAN.location(), new ColumnBounds(45, 1)),
                Map.entry(Biomes.FROZEN_OCEAN.location(), new ColumnBounds(46, 1)),
                Map.entry(Biomes.DEEP_FROZEN_OCEAN.location(), new ColumnBounds(47, 1)),
                Map.entry(Biomes.MUSHROOM_FIELDS.location(), new ColumnBounds(48, 1)),
                Map.entry(Biomes.DRIPSTONE_CAVES.location(), new ColumnBounds(49, 1)),
                Map.entry(Biomes.LUSH_CAVES.location(), new ColumnBounds(50, 1)),
                Map.entry(Biomes.NETHER_WASTES.location(), new ColumnBounds(51, 1)),
                Map.entry(Biomes.WARPED_FOREST.location(), new ColumnBounds(52, 1)),
                Map.entry(Biomes.CRIMSON_FOREST.location(), new ColumnBounds(53, 1)),
                Map.entry(Biomes.SOUL_SAND_VALLEY.location(), new ColumnBounds(54, 1)),
                Map.entry(Biomes.BASALT_DELTAS.location(), new ColumnBounds(55, 1)),
                Map.entry(Biomes.THE_END.location(), new ColumnBounds(56, 1)),
                Map.entry(Biomes.END_HIGHLANDS.location(), new ColumnBounds(57, 1)),
                Map.entry(Biomes.END_MIDLANDS.location(), new ColumnBounds(58, 1)),
                Map.entry(Biomes.SMALL_END_ISLANDS.location(), new ColumnBounds(59, 1)),
                Map.entry(Biomes.END_BARRENS.location(), new ColumnBounds(60, 1)),
                // 1.19
                Map.entry(Biomes.MANGROVE_SWAMP.location(), new ColumnBounds(61, 1)),
                Map.entry(Biomes.DEEP_DARK.location(), new ColumnBounds(62, 1)),
                // 1.20
                Map.entry(Biomes.CHERRY_GROVE.location(), new ColumnBounds(63, 1))
        );
    }

}
