package vanadium.util;

import vanadium.colormapping.BiomeColorMap;
import vanadium.colormapping.ExtendedChromaticRegistryResolver;
import vanadium.colormapping.IVanadiumRegistryResolver;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class ColorMappingStorage<T> {
    private final Table<T, Identifier, BiomeColorMap> _colorMappings;
    private final Map<T, BiomeColorMap> _fallbackColorMappings;
    private final Map<T, ExtendedChromaticRegistryResolver> _managers;
    private final Map<T, IVanadiumRegistryResolver> _defaultRegistryManagers;
    private final ChromatiqResolverProvider<T> _defaultResolverProvider;

    public ColorMappingStorage(ChromatiqResolverProvider<T> defaultResolverProvider) {
        _colorMappings = HashBasedTable.create();
        _fallbackColorMappings = new HashMap<>();
        _managers = new HashMap<>();
        _defaultRegistryManagers = new HashMap<>();
        _defaultResolverProvider = defaultResolverProvider;
    }
}
