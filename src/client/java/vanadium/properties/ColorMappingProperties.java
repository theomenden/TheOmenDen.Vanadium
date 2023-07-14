package vanadium.properties;

import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vanadium.enums.ColumnLayout;
import vanadium.enums.Format;
import vanadium.util.ColumnBounds;

import java.util.Collection;
import java.util.Map;

public class ColorMappingProperties {

    private static final Logger log = LogManager.getLogger();
    private transient final boolean isUsingOptifine;
    private final Format format;
    private final Collection<ApplicableBlockStates> blockStates;
    private final Identifier source;
    private ColumnLayout layout;
    private final int yVariance;
    private final int yOffset;
    private final Map<Identifier, ColumnBounds> columnsbyBiome;

    private ColorMappingProperties(Identifier identifier, Settings){}

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

