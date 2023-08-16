package vanadium.models;

import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vanadium.Vanadium;
import vanadium.defaults.DefaultColumns;
import vanadium.models.enums.ColumnLayout;
import vanadium.models.enums.Format;
import vanadium.models.records.ColumnBounds;
import vanadium.models.records.VanadiumColor;
import vanadium.utils.BiomeTracingUtils;
import vanadium.utils.GsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ColorMappingProperties {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Identifier id;
    private transient final boolean isUsingOptfine;
    @Getter
    private final Format format;
    private final Collection<ApplicableBlockStates> blocks;
    @Getter
    private final Identifier source;
    @Getter
    private final VanadiumColor color;
    private final ColumnLayout layout;
    @Getter
    private final int yVariance;
    @Getter
    private final int yOffset;
    private final Map<Identifier, ColumnBounds> columnsByBiome;

    private static final ColumnBounds DEFAULT_BOUNDS = new ColumnBounds(0, 1);

    private ColorMappingProperties(Identifier id, Settings settings) {
        this.id = id;
        this.isUsingOptfine = this.id
                .getPath()
                .endsWith(".properties");
        this.format = settings.format;
        this.blocks = settings.blocks;
        Identifier source = Identifier.tryParse(settings.source);

        if (source == null) {
            LOGGER.error("{}: Invalid source location '{}', using file name as fallback", id, settings.source);
            source = new Identifier(makeSourceFromFileName(id));
        }

        this.source = source;
        this.color = settings.color;
        this.layout = Objects.requireNonNullElse(settings.layout, this.isUsingOptfine ? ColumnLayout.OPTIFINE : ColumnLayout.DEFAULT);
        this.yVariance = settings.yVariance;
        this.yOffset = settings.yOffset;

        if (settings.grid != null) {
            this.columnsByBiome = new HashMap<>();
            int nextColumn = 0;
            List<GridEntry> grid = settings.grid;
            for (GridEntry entry : grid) {
                if (entry.column >= 0) {
                    nextColumn = entry.column;
                }
                ColumnBounds bounds = new ColumnBounds(nextColumn, entry.width);
                nextColumn += entry.width;
                entry.biomes
                        .stream()
                        .map(biomeId -> BiomeTracingUtils.updateBiomeName(biomeId, this.id))
                        .filter(Objects::nonNull)
                        .forEach(updated -> columnsByBiome.put(updated, bounds));
            }
        } else if (settings.biomes != null) {
            this.columnsByBiome = new HashMap<>();
            settings.biomes
                    .forEach((key, value) -> {
                        Identifier updated = BiomeTracingUtils.updateBiomeName(key, this.id);
                        if (updated != null) {
                            columnsByBiome.put(updated, new ColumnBounds(value, 1));
                        }
                    });
        } else {
            this.columnsByBiome = null;
        }
    }

    public ColumnBounds getColumn(RegistryKey<Biome> biomeKey, Registry<Biome> biomeRegistry) {
        if(format == Format.GRID) {
            if(biomeKey != null) {
                Identifier id = biomeKey.getValue();
                if(columnsByBiome != null) {
                    ColumnBounds cb = columnsByBiome.get(id);

                    if(cb == null) {
                        throw new IllegalStateException(id.toString());
                    }
                    return cb;
                } else {
                    return switch(layout) {
                        case DEFAULT ->  DefaultColumns.getDefaultBoundaries(biomeKey);
                        case OPTIFINE -> DefaultColumns.getOptifineBoundaries(biomeKey, biomeRegistry);
                        case LEGACY -> DefaultColumns.getLegacyBoundaries(biomeKey, biomeRegistry, this.isUsingOptfine);
                        case STABLE -> DefaultColumns.getStableBoundaries(biomeKey);
                    };
                }
            } else{
                return DEFAULT_BOUNDS;
            }
        } else {
            throw new IllegalStateException(format.toString());
        }
    }

    public Set<Identifier> getApplicableBiomes() {
        if (columnsByBiome == null || columnsByBiome.isEmpty()) {
            return Sets.newHashSet();
        }
        return new HashSet<>(columnsByBiome.keySet());
    }

    public Set<Block> getApplicableBlocks() {
        return blocks
                .stream()
                .filter(a -> a.specialKey == null && a.states.isEmpty())
                .map(a -> a.block)
                .collect(Collectors.toSet());
    }

    public Set<BlockState> getApplicableBlockStates() {
        return blocks
                .stream()
                .filter(a -> a.specialKey == null)
                .flatMap(a -> a.states.stream())
                .collect(Collectors.toSet());
    }

    public Map<Identifier, Collection<Identifier>> getApplicableSpecialIds() {
       return blocks
                .stream()
                .filter(a -> a.specialKey != null)
                .collect(Collectors.toMap(a -> a.specialKey, a -> a.specialIds, (a1, b) -> b));
    }

    @Override
    public String toString() {
        return String.format("ColormapProperties { format=%s, blocks=%s, source=%s, color=%x, yVariance=%x, yOffset=%x }",
                format,
                blocks,
                source,
                color == null ? 0 : color.rgb(),
                yVariance,
                yOffset);
    }

    public static ColorMappingProperties load(ResourceManager manager, Identifier id, boolean custom) {
        try (InputStream in = manager
                .getResourceOrThrow(id)
                .getInputStream();
             Reader r = GsonUtils
                     .getJsonReader(in, id, k -> k, "blocks"::equals)) {
            return loadFromJson(r, id, custom);
        } catch (IOException e) {
            return loadFromJson(new StringReader("{}"), id, custom);
        }
    }

    private static ColorMappingProperties loadFromJson(Reader json, Identifier id, boolean custom) {
        Settings settings;
        try {
            settings = GsonUtils.PROPERTY_GSON.fromJson(json, Settings.class);
            if (settings == null) {
                settings = new Settings();
            }
        } catch (Exception e) {
            // any one of a number of exceptions could have been thrown during deserialization
            LOGGER.error("Error loading {}: {}", id, e.getMessage());
            settings = new Settings();
        }
        if (settings.format == null) {
            settings.format = Vanadium.COLOR_PROPERTIES
                    .getProperties()
                    .getDefaultFormat();
        }
        if (settings.layout == null) {
            settings.layout = Vanadium.COLOR_PROPERTIES
                    .getProperties()
                    .getDefaultColumnLayout();
        }
        if (custom) {
            if (settings.blocks == null) {
                String blockId = id.getPath();
                blockId = blockId.substring(blockId.lastIndexOf('/') + 1, blockId.lastIndexOf('.'));
                settings.blocks = new ArrayList<>();
                try {
                    settings.blocks.add(GsonUtils.PROPERTY_GSON.fromJson(blockId, ApplicableBlockStates.class));
                } catch (JsonSyntaxException e) {
                    LOGGER.error("Error parsing {}: {}", id, e.getMessage());
                }
            }
        } else {
            // disable `blocks`, `grid`, and `biomes` for non-custom colormaps, warn if they are present
            if (settings.biomes != null || settings.grid != null || settings.blocks != null) {
                LOGGER.warn("{}: found `biomes`, `grid`, or `blocks` properties in a provided colormap; these will be ignored", id);
            }
            settings.biomes = null;
            settings.grid = null;
            settings.blocks = Collections.emptyList();
        }
        if (settings.source == null) {
            settings.source = makeSourceFromFileName(id);
        }
        settings.source = GsonUtils.resolveRelativeResourceLocation(settings.source, id);
        return new ColorMappingProperties(id, settings);
    }

    private static String makeSourceFromFileName(Identifier id) {
        String path = id.toString();
        path = path.substring(0, path.lastIndexOf('.')) + ".png";
        return path;
    }

    private static class Settings {
        Format format = null;
        Collection<ApplicableBlockStates> blocks = null;
        String source = null;
        VanadiumColor color = null;
        ColumnLayout layout = null;
        int yVariance = 0;
        int yOffset = 0;
        Map<Identifier, Integer> biomes = null;
        List<GridEntry> grid = null;
    }
}
