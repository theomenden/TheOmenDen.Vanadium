package vanadium.utils;

import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import vanadium.Vanadium;

import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;

public final class BiomeTracingUtils {
    private static final Logger log = LogManager.getLogger(Vanadium.MODID);
    private static final Set<Identifier> REMOVED_BIOMES = createRemovedBiomeSet();
    private static final Map<Identifier, Identifier> RENAMED_BIOMES = createRenamedBiomeMap();

    @Nullable
    public static Identifier updateBiomeName(Identifier biomeId, Identifier context) {
        if(REMOVED_BIOMES.contains(biomeId)){
            log.warn("Biome '{}' does not exist and should be removed: {}", context, biomeId);
            return null;
        }

        Identifier renamed = RENAMED_BIOMES.get(biomeId);

        if(renamed != null){
            log.warn("Biome '{}' has been renamed to '{}' and needs to be updated: {}", context, biomeId, renamed);
            return renamed;
        }

        return biomeId;
    }

    private static Map<Identifier, Identifier> createRenamedBiomeMap() {
        return Map.ofEntries(
                Map.entry(new Identifier("mountains"), new Identifier("windswept_hills")),
                Map.entry(new Identifier("snowy_tundra"), new Identifier("snowy_plains")),
                Map.entry(new Identifier("jungle_edge"), new Identifier("sparse_jungle")),
                Map.entry(new Identifier("stone_shore"), new Identifier("stony_shore")),
                Map.entry(new Identifier("giant_tree_taiga"), new Identifier("old_growth_pine_taiga")),
                Map.entry(new Identifier("wooded_mountains"), new Identifier("windswept_forest")),
                Map.entry(new Identifier("wooded_badlands_plateau"), new Identifier("wooded_badlands")),
                Map.entry(new Identifier("gravelly_mountains"), new Identifier("windswept_gravelly_hills")),
                Map.entry(new Identifier("tall_birch_forest"), new Identifier("old_growth_birch_forest")),
                Map.entry(new Identifier("giant_spruce_taiga"), new Identifier("old_growth_spruce_taiga")),
                Map.entry(new Identifier("shattered_savanna"), new Identifier("windswept_savanna"))
        );
    }

    private static Set<Identifier> createRemovedBiomeSet() {
        return Set.of(
                new Identifier("snowy_taiga_mountains"),
                new Identifier("giant_tree_taiga_hills"),
                new Identifier("taiga_hills"),
                new Identifier("modified_gravelly_mountains"),
                new Identifier("desert_hills"),
                new Identifier("snowy_taiga_hills"),
                new Identifier("jungle_hills"),
                new Identifier("mushroom_field_shore"),
                new Identifier("shattered_savanna_plateau"),
                new Identifier("mountain_edge"),
                new Identifier("wooded_hills"),
                new Identifier("bamboo_jungle_hills"),
                new Identifier("modified_wooded_badlands_plateau"),
                new Identifier("desert_lakes"),
                new Identifier("dark_forest_hills"),
                new Identifier("birch_forest_hills"),
                new Identifier("modified_badlands_plateau"),
                new Identifier("badlands_plateau"),
                new Identifier("modified_jungle_edge"),
                new Identifier("giant_spruce_taiga_hills"),
                new Identifier("swamp_hills"),
                new Identifier("modified_jungle"),
                new Identifier("tall_birch_hills"),
                new Identifier("snowy_mountains"),
                new Identifier("deep_warm_ocean")
        );
    }
}
