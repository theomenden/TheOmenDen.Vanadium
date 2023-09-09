package vanadium.customcolors;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;
import vanadium.biomeblending.storage.ColorMappingStorage;
import vanadium.customcolors.interfaces.VanadiumResolver;
import vanadium.customcolors.mapping.BiomeColorMapping;
import vanadium.models.records.Coordinates;
import vanadium.models.NonBlockingThreadLocal;
import vanadium.models.YCoordinate;

public final class VanadiumExtendedColorResolver implements ColorResolver {
    @Nullable private static RegistryAccess registryManager;
    private final ThreadLocal<YCoordinate> positionY;
    @Getter
    private final VanadiumResolver wrappedResolver;

    public <T> VanadiumExtendedColorResolver(ColorMappingStorage<T> storage, T key, VanadiumResolver fallback) {
        this.positionY = NonBlockingThreadLocal.withInitial(YCoordinate::new);
        this.wrappedResolver = createResolver(storage, key, fallback);
    }

    public VanadiumExtendedColorResolver(VanadiumResolver wrappedResolver) {
        this.positionY = NonBlockingThreadLocal.withInitial(YCoordinate::new);
        this.wrappedResolver = wrappedResolver;
    }

    public int resolveExtendedColor(BlockAndTintGetter world, BlockPos position) {
        this.positionY.get().Y = position.getY();
        return world.getBlockTint(position, this);
    }

    @Override
    public int getColor(Biome biome, double x, double z) {
        var coordinates = new Coordinates((int)x, this.positionY.get().Y, (int)z);
        return 0xfffefefe & wrappedResolver.getColorAtCoordinatesForBiome(registryManager, biome, coordinates);
    }

    public static void setRegistryManager(@Nullable RegistryAccess manager) {
        registryManager = manager;
    }

    private static <T> VanadiumResolver createResolver(ColorMappingStorage<T> storage, T key, VanadiumResolver fallback) {

        var data = NonBlockingThreadLocal.withInitial(StoredData::new);

        return (manager, biome, coordinates) -> {
            var storedData = data.get();
            if(storedData.lastBiome != biome) {
                storedData.lastColormapping = storage.getColorMapping(manager, key, biome);
                storedData.lastBiome = biome;
            }

            var colormapping = storedData.lastColormapping;
            return colormapping != null
                    ? colormapping.getColorAtCoordinatesForBiome(manager, biome, coordinates)
                    : fallback.getColorAtCoordinatesForBiome(manager, biome, coordinates);
        };
    }

    private static final class StoredData {
        @Nullable Biome lastBiome;
        @Nullable BiomeColorMapping lastColormapping;
    }
}
