package vanadium.properties;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vanadium.enums.ColumnLayout;
import vanadium.enums.Format;
import vanadium.models.GridEntry;
import vanadium.util.ColumnBounds;
import vanadium.util.GsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;

public class ColorMappingProperties {

    private static final Logger log = LogManager.getLogger();
    private transient final boolean isUsingOptifine;

    private final Identifier identifier;
    private final Format format;
    private final Collection<ApplicableBlockStates> blockStates;
    private final Identifier source;
    private ColumnLayout layout;
    private final int yVariance;
    private final int yOffset;
    private final Map<Identifier, ColumnBounds> columnsByBiome;

    private static final ColumnBounds DEFAULT_COLUMN_BOUNDARIES = new ColumnBounds(0, 1);

    private ColorMappingProperties(Identifier identifier, Settings settings){
        this.identifier = identifier;
        this.isUsingOptifine = this.identifier.getPath().endsWith(".properties");
        this.blockStates = new ArrayList<>();
        this.format = settings.format;
        this.source = null;
        this.layout = null;
        this.yVariance = 0;
        this.yOffset = 0;
        this.columnsByBiome = new HashMap<>();
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public Format getFormat() {
        return this.format;
    }

    public Identifier getSource() {
        return this.source;
    }

    public int getYVariance() {
        return this.yVariance;
    }

    public int getYOffset() {
        return this.yOffset;
    }

    public ColumnBounds getColumn(RegistryKey<Biome> biomeKey, Registry<Biome> biomeRegistry){
        if(this.format != Format.GRID) {
            throw new IllegalStateException("Column layout is not set to GRID");
        }

        if(biomeKey == null) {
            return DEFAULT_COLUMN_BOUNDARIES;
        }

        Identifier biomeId = biomeKey.getValue();

        if(this.columnsByBiome != null) {
            ColumnBounds column = this.columnsByBiome.get(biomeId);
            if(column!= null) {
                return column;
            }
            throw new IllegalArgumentException("Biome " + biomeId + " not found in color mapping");
        }

        return switch(this.layout) {
            case DEFAULT -> DefaultColumns.getDefaultBoundaries(biomeKey);
            case OPTIFINE -> DefaultColumns.getOptifineBoundaries(biomeKey, biomeRegistry);
            case LEGACY -> DefaultColumns.getLegacyBoundaries(biomeKey, biomeRegistry, this.isUsingOptifine);
            case STABLE -> DefaultColumns.getStableBoundaries(biomeKey);
        };
    }

    public static ColorMappingProperties loadProperties(ResourceManager manager, Identifier identifier, boolean isCustom) {
        try(InputStream stream = manager.getResourceOrThrow(identifier).getInputStream()) {

        } catch(IOException e) {
            return loadFromJson();
        }
    }

    private static ColorMappingProperties loadFromJson(Reader jsonReader, Identifier identifier, boolean isCustom) {
     Settings settings;

     try{
         settings = GsonUtils.PROPERTY_GSON.fromJson(jsonReader, Settings.class);

         if(settings == null) {
             settings = new Settings();
         }

     } catch(Exception e) {
         log.error("Failed to load color mapping properties {}: {}", identifier, e.getMessage());
         settings = new Settings();
     }



     return new ColorMappingProperties(identifier, settings);
    }

    private static class Settings {
        Format format = null;
        Collection<ApplicableBlockStates> blockStates = null;
        String source = null;
        ColumnLayout = null;
        int yVariance = 0;
        int yOffset = 0;
        Map<Identifier, Integer> biomes = null;
        List<GridEntry> grid =null;
    }
}

