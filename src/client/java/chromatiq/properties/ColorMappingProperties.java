package chromatiq.properties;

import chromatiq.enums.ColumnLayout;
import chromatiq.util.ColumnBounds;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.Format;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class ColorMappingProperties {
    private static final Logger log  = LogManager.getLogger();
    private final Identifier id;
    private transient final boolean isUsingOptifine;
    private final Format format;
    private final Collection<ApplicableBlockStates> blocks;

    private final Identifier source;

    private final ColumnLayout columnLayout;

    private final int yVariance;
    private final int yOffset;

    private final Map<Identifier, ColumnBounds> columnsByBiome;

    private static final ColumnBounds DEFAULT_BOUNDS = new ColumnBounds(0, 1);

    public Map<Identifier, Collection<Identifier>> getApplicableSpecialIds() {
       return blocks.stream()
                .filter(a -> a.specialKey != null)
               .collect(Collectors.toMap(a -> a.specialKey, a -> a.specialIds));
    }


       private static String makeSourceFromFileName(Identifier id) {
        String path = id.toString();
        path = path.substring(0, path.lastIndexOf('.')) + ".png";
        return path;
    }
}
