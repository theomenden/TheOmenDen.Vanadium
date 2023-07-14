package vanadium.util;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import vanadium.Vanadium;
import vanadium.colormapping.BiomeColorMap;
import vanadium.resolvers.ExtendedColorResolver;
import vanadium.resolvers.VanadiumRegistryResolver;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.util.Identifier;
import vanadium.resolvers.VanadiumResolver;
import vanadium.resolvers.VanadiumResolverProvider;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ColorMappingStorage<K> {
    private final Table<K, Identifier, BiomeColorMap> colorMappings;
    private final Map<K, BiomeColorMap> fallbackColorMappings;
    private final Map<K, ExtendedColorResolver> managers;
    private final Map<K, VanadiumResolver> defaultResolvers;
    private final VanadiumResolverProvider<K> defaultResolverProvider;

    public ColorMappingStorage(VanadiumResolverProvider<K> defaultResolverProvider) {
        this.colorMappings = HashBasedTable.create();
        this.fallbackColorMappings = new HashMap<>();
       this.managers = new HashMap<>();
        this.defaultResolvers = new HashMap<>();
        this.defaultResolverProvider = defaultResolverProvider;
    }

    @Nullable
    public BiomeColorMap getBiomeColorMapping(DynamicRegistryManager registryManager, K key, Biome biome) {
        BiomeColorMap resolvedMap = this.colorMappings.get(key, Vanadium.getBiomeId(registryManager, biome));

        if(resolvedMap == null) {
            resolvedMap = this.fallbackColorMappings.get(key);
        }
        return resolvedMap;
    }

    @Nullable
    public BiomeColorMap getFallbackColorMap(K key) {
        return this.fallbackColorMappings.get(key);
    }

    @Nullable
    public ExtendedColorResolver getResolver(K key) {
        return this.managers.get(key);
    }

    public VanadiumResolver getVanadiumResolver(K key) {
        var extendedResolver = getResolver(key);

        if(extendedResolver != null) {
            return extendedResolver.getWrappedResolver();
        }
        return defaultResolvers.computeIfAbsent(key, defaultResolverProvider::create);
    }

    public boolean containsColorMapping(K key) {
        return !colorMappings.row(key).isEmpty() || fallbackColorMappings.containsKey(key);
    }

    public void addColorMapping(BiomeColorMap colorMapping, Collection<? extends K> keys, Set<? extends Identifier> biomes) {
        if(biomes.isEmpty()) {
            keys.forEach(key -> {
                fallbackColorMappings.put(key, colorMapping);
                managers.put(key, new ExtendedColorResolver(this, key, defaultResolvers.computeIfAbsent(key, defaultResolverProvider::create)));
            });
        } else {
            keys.forEach(key -> {
                for (Identifier biomeId : biomes) {
                    colorMappings.put(key, biomeId, colorMapping);
                }
                managers.put(key, new ExtendedColorResolver(this, key, defaultResolvers.computeIfAbsent(key, defaultResolverProvider::create)));
            });
        }
    }

    public void clearFields() {
        colorMappings.clear();
        fallbackColorMappings.clear();
        managers.clear();
        defaultResolvers.clear();
    }
}
