package vanadium.biomeblending.storage;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import net.minecraft.util.Identifier;
import vanadium.customcolors.ExtendedColorResolver;
import vanadium.customcolors.interfaces.VanadiumResolver;
import vanadium.customcolors.interfaces.VanadiumResolverProvider;

import java.util.Map;

public class ColorMappingStorage<T> {
    private final Table<T, Identifier, BiomeColorMapping> colorMappings;
    private final Map<T, BiomeColorMapping> fallbackColorMappings;
    private final Map<T, ExtendedColorResolver> resolvers;
    private final Map<T, VanadiumResolver> defaultResolvers;
    private final VanadiumResolverProvider<T> defaultResolverProvider;

    ColorMappingStorage(VanadiumResolverProvider<T> defaultResolverProvider) {
        this.colorMappings = HashBasedTable.create();
        this.fallbackColorMappings = Maps.newHashMap();
        this.resolvers = Maps.newHashMap();
        this.defaultResolvers = Maps.newHashMap();
        this.defaultResolverProvider = defaultResolverProvider;
    }


}
