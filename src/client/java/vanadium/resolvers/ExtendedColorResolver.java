package vanadium.resolvers;


import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;
import vanadium.colormapping.BiomeColorMap;
import vanadium.models.Coordinates;
import vanadium.models.NonBlockingThreadLocal;
import vanadium.util.ColorMappingStorage;

public class ExtendedColorResolver implements ColorResolver {
    @Nullable private static  RegistryAccess.ImmutableRegistryAccess registryManager;
    private final ThreadLocal<CoordinateY> yPosition;
    private final VanadiumResolver wrappedResolver;

    public <K> ExtendedColorResolver(ColorMappingStorage<K> storage, K key, VanadiumResolver fallbackResolver) {
        this.yPosition = NonBlockingThreadLocal.withInitial(CoordinateY::new);
        this.wrappedResolver = createResolver(storage, key, fallbackResolver);
    }

    public ExtendedColorResolver(VanadiumResolver wrappedResolver) {
        this.yPosition = NonBlockingThreadLocal.withInitial(CoordinateY::new);
        this.wrappedResolver = wrappedResolver;
    }

    public int resolveExtendedColor(BlockAndTintGetter world, BlockPos position) {
        this.yPosition.get().y = position.getY();
        return world.getBlockTint(position, this);
    }

    public VanadiumResolver getWrappedResolver() {
        return this.wrappedResolver;
    }

    @Override
    public int getColor(Biome biome, double x, double z) {
        return 0xffefefe & wrappedResolver.getColorAtCoordinatesForBiomeByManager(registryManager,
                biome,
                new Coordinates((int)x, this.yPosition.get().y, (int)z));
    }

    public static void setRegistryManager(@Nullable RegistryAccess.ImmutableRegistryAccess manager) {
        registryManager = manager;
    }

    private static <K> VanadiumResolver createResolver(ColorMappingStorage<K> storage, K key, VanadiumResolver fallbackResolver) {
        var data = NonBlockingThreadLocal.withInitial(StoredBiomeData::new);
        return (manager, biome, coordinates) -> {
            var storedData = data.get();
            if(storedData.lastBiome != biome) {
                storedData.lastColorMapping = storage.getBiomeColorMapping(manager, key, biome);
                storedData.lastBiome = biome;
            }

            var colorMapping = storedData.lastColorMapping;
            return colorMapping != null
                    ? colorMapping.getColorAtCoordinatesForBiomeByManager(manager, biome, coordinates)
                    : fallbackResolver.getColorAtCoordinatesForBiomeByManager(manager, biome, coordinates);
        };
    }

    private static final class StoredBiomeData {
        @Nullable Biome lastBiome;
        @Nullable BiomeColorMap lastColorMapping;
    }

    private static final class CoordinateY {
        int y;
    }

}
