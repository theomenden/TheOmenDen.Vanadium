package vanadium.customcolors;

import lombok.Getter;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.ColorResolver;
import org.jetbrains.annotations.Nullable;
import vanadium.biomeblending.storage.ColorMappingStorage;
import vanadium.customcolors.interfaces.VanadiumResolver;
import vanadium.customcolors.mapping.BiomeColorMapping;
import vanadium.models.Coordinates;
import vanadium.models.NonBlockingThreadLocal;
import vanadium.models.YCoordinate;

public class ExtendedColorResolver implements ColorResolver {
    @Nullable private static DynamicRegistryManager registryManager;
    private final YCoordinate positionY;
    @Getter
    private final VanadiumResolver wrappedResolver;

    public <K> ExtendedColorResolver(ColorMappingStorage<K> storage, K key, VanadiumResolver fallback) {
        this.positionY = new YCoordinate(0);
        this.wrappedResolver = createResolver(storage, key, fallback);
    }

    public ExtendedColorResolver(VanadiumResolver wrappedResolver) {
        this.positionY = new YCoordinate(0);
        this.wrappedResolver = wrappedResolver;
    }



    @Override
    public int getColor(Biome biome,double x, double z) {
        var coordinates = new Coordinates((int)x, this.positionY.Y(), (int)z);
        return 0xfffefefe & wrappedResolver.getColorAtCoordinatesForBiome(registryManager, biome, coordinates);
    }

    public static void setRegistryManager(@Nullable DynamicRegistryManager manager) {
        registryManager = manager;
    }

    private static <T> VanadiumResolver createResolver(ColorMappingStorage<T> storage, T key, VanadiumResolver fallback) {
        final class StoredData {
            @Nullable Biome lastBiome;
            @Nullable BiomeColorMapping lastColormapping;
        }

        var data = NonBlockingThreadLocal.withInitial(StoredData::new);

        return (manager, biome, coordinates) -> {
            var storedData = data.get();
            if(storedData.lastBiome != biome) {
                storedData.lastColormapping = storage.getColorMapping(manager, key, biome);
                storedData.lastBiome = biome;
            }

            var colormapping = storedData.lastColormapping;
            return colormapping != null
                    ? colormapping.getColor(manager, biome, coordinates)
                    : fallback.getColorAtCoordinatesForBiome(manager, biome, coordinates);
        }

    }
}
