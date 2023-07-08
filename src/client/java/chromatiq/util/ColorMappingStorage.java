package chromatiq.util;

import chromatiq.colormapping.BiomeColorMap;
import chromatiq.colormapping.ExtendedChromaticRegistryResolver;
import chromatiq.colormapping.IChromaticRegistryResolver;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class ColorMappingStorage<T> {
    private final Table<T, Identifier, BiomeColorMap> _colorMappings;
    private final Map<T, BiomeColorMap> _fallbackColorMappings;
    private final Map<T, ExtendedChromaticRegistryResolver> _managers;
    private final Map<T, IChromaticRegistryResolver> _defaultRegistryManagers;
    private final ChromatiqResolverProvider<T> _defaultResolverProvider;

    public ColorMappingStorage(ChromatiqResolverProvider<T> defaultResolverProvider) {
        _colorMappings = HashBasedTable.create();
        _fallbackColorMappings = new HashMap<>();
        _managers = new HashMap<>();
        _defaultRegistryManagers = new HashMap<>();
        _defaultResolverProvider = defaultResolverProvider;
    }
}
