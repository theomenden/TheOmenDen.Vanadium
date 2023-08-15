package vanadium.biomeblending.storage;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import vanadium.Vanadium;
import vanadium.customcolors.ExtendedColorResolver;
import vanadium.customcolors.interfaces.VanadiumResolver;
import vanadium.customcolors.interfaces.VanadiumResolverProvider;
import vanadium.customcolors.mapping.BiomeColorMapping;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ColorMappingStorage<T> {
    private final Table<T, Identifier, BiomeColorMapping> colorMappings;
    private final Map<T, BiomeColorMapping> fallbackColorMappings;
    private final Map<T, ExtendedColorResolver> resolvers;
    private final Map<T, VanadiumResolver> defaultResolvers;
    private final VanadiumResolverProvider<T> defaultResolverProvider;

    public ColorMappingStorage(VanadiumResolverProvider<T> defaultResolverProvider) {
        this.colorMappings = HashBasedTable.create();
        this.fallbackColorMappings = Maps.newHashMap();
        this.resolvers = Maps.newHashMap();
        this.defaultResolvers = Maps.newHashMap();
        this.defaultResolverProvider = defaultResolverProvider;
    }

    @Nullable
    public BiomeColorMapping getColorMapping(DynamicRegistryManager manager, T key, Biome biome) {
        var resultingMapping = this.colorMappings.get(key, Vanadium.getBiomeIdentifier(manager, biome));

        return resultingMapping == null
                ? this.fallbackColorMappings.get(key)
                : resultingMapping;
    }

    @Nullable
    public BiomeColorMapping getFallbackColorMapping(T key) {
        return this.fallbackColorMappings.get(key);
    }

    @Nullable
    public ExtendedColorResolver getExtendedResolver(T key) {
        return this.resolvers.get(key);
    }

    @Nullable
    public VanadiumResolver getVanadiumResolver(T key) {
        var extendedResolver = getExtendedResolver(key);

        return extendedResolver != null
                ? extendedResolver.getWrappedResolver()
                : defaultResolvers.computeIfAbsent(key, defaultResolverProvider::create);
    }

    public boolean contains(T key) {
        return !colorMappings.row(key).isEmpty() || fallbackColorMappings.containsKey(key);
    }

    public void addColorMapping(BiomeColorMapping colorMapping, Collection<? extends T> keys, Set<? extends Identifier> biomes) {
        if(biomes.isEmpty()){
            keys
                    .forEach(key -> {
                        fallbackColorMappings.put(key, colorMapping);
                        resolvers.put(key, new ExtendedColorResolver(this, key, defaultResolvers.computeIfAbsent(key, defaultResolverProvider::create)));
                    });
                        return;
        }

        keys.forEach(key -> {
            biomes.forEach(b -> colorMappings.put(key, b, colorMapping));
            resolvers.put(key, new ExtendedColorResolver(this, key, defaultResolvers.computeIfAbsent(key, defaultResolverProvider::create)));
        });

    }

    public void clearMappings() {
        colorMappings.clear();
        fallbackColorMappings.clear();
        resolvers.clear();
        defaultResolvers.clear();
    }
}
