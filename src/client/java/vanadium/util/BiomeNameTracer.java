package vanadium.util;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import vanadium.entry.Vanadium;

import java.util.Set;
import java.util.Map;

import static java.util.Map.entry;

public final class BiomeNameTracer {
    private static final Logger log = LogManager.getLogger(Vanadium.MODID);
    private static final Set<ResourceLocation> REMOVED_BIOMES = createRemovedBiomeSet();
    private static final Map<ResourceLocation, ResourceLocation> RENAMED_BIOMES = createRenamedBiomeMap();

    @Nullable
    public static ResourceLocation updateBiomeName(ResourceLocation biomeId, ResourceLocation context) {
      if(REMOVED_BIOMES.contains(biomeId)){
          log.warn("Biome '{}' does not exist and should be removed: {}", context, biomeId);
          return null;
      }

        ResourceLocation renamed = RENAMED_BIOMES.get(biomeId);

      if(renamed != null){
          log.warn("Biome '{}' has been renamed to '{}' and needs to be updated: {}", context, biomeId, renamed);
          return renamed;
      }

      return biomeId;
    }

    private static Map<ResourceLocation, ResourceLocation> createRenamedBiomeMap() {
        return Map.ofEntries(
                entry(new ResourceLocation("mountains"), new ResourceLocation("windswept_hills")),
                entry(new ResourceLocation("snowy_tundra"), new ResourceLocation("snowy_plains")),
                entry(new ResourceLocation("jungle_edge"), new ResourceLocation("sparse_jungle")),
                entry(new ResourceLocation("stone_shore"), new ResourceLocation("stony_shore")),
                entry(new ResourceLocation("giant_tree_taiga"), new ResourceLocation("old_growth_pine_taiga")),
                entry(new ResourceLocation("wooded_mountains"), new ResourceLocation("windswept_forest")),
                entry(new ResourceLocation("wooded_badlands_plateau"), new ResourceLocation("wooded_badlands")),
                entry(new ResourceLocation("gravelly_mountains"), new ResourceLocation("windswept_gravelly_hills")),
                entry(new ResourceLocation("tall_birch_forest"), new ResourceLocation("old_growth_birch_forest")),
                entry(new ResourceLocation("giant_spruce_taiga"), new ResourceLocation("old_growth_spruce_taiga")),
                entry(new ResourceLocation("shattered_savanna"), new ResourceLocation("windswept_savanna"))
            );
    }

    private static Set<ResourceLocation> createRemovedBiomeSet() {
        return Set.of(
                new ResourceLocation("snowy_taiga_mountains"),
                new ResourceLocation("giant_tree_taiga_hills"),
                new ResourceLocation("taiga_hills"),
                new ResourceLocation("modified_gravelly_mountains"),
                new ResourceLocation("desert_hills"),
                new ResourceLocation("snowy_taiga_hills"),
                new ResourceLocation("jungle_hills"),
                new ResourceLocation("mushroom_field_shore"),
                new ResourceLocation("shattered_savanna_plateau"),
                new ResourceLocation("mountain_edge"),
                new ResourceLocation("wooded_hills"),
                new ResourceLocation("bamboo_jungle_hills"),
                new ResourceLocation("modified_wooded_badlands_plateau"),
                new ResourceLocation("desert_lakes"),
                new ResourceLocation("dark_forest_hills"),
                new ResourceLocation("birch_forest_hills"),
                new ResourceLocation("modified_badlands_plateau"),
                new ResourceLocation("badlands_plateau"),
                new ResourceLocation("modified_jungle_edge"),
                new ResourceLocation("giant_spruce_taiga_hills"),
                new ResourceLocation("swamp_hills"),
                new ResourceLocation("modified_jungle"),
                new ResourceLocation("tall_birch_hills"),
                new ResourceLocation("snowy_mountains"),
                new ResourceLocation("deep_warm_ocean")
        );
    }
}
