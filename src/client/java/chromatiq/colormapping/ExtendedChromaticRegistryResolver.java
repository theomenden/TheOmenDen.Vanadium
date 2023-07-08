package chromatiq.colormapping;

import chromatiq.models.Coordinates;
import chromatiq.util.ColorMappingStorage;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.ColorResolver;
import org.jetbrains.annotations.Nullable;

public final class ExtendedChromaticRegistryResolver implements IChromaticRegistryResolver, ColorResolver {

    @Nullable
    private static DynamicRegistryManager dynamicRegistryManager;

    private final ThreadLocal<YCoordinate> yPosition;
    private final IChromaticRegistryResolver wrappedRegistryManager;

    <T> ExtendedChromaticRegistryResolver(ColorMappingStorage<T> storage, T key, IChromaticRegistryResolver fallbackResolver) {
        yPosition = ThreadLocal.withInitial(YCoordinate::new);
        wrappedRegistryManager = createResolver(storage, key, fallbackResolver);
    }

    ExtendedChromaticRegistryResolver(IChromaticRegistryResolver wrappedRegistryManager) {
        yPosition = ThreadLocal.withInitial(YCoordinate::new);
        this.wrappedRegistryManager = wrappedRegistryManager;
    }


    @Override
    public int getColorRegistryForDynamicPosition(DynamicRegistryManager dynamicRegistryManager, Biome biome, Coordinates coordinates) {
        return 0xffefefe & wrappedRegistryManager.getColorRegistryForDynamicPosition(dynamicRegistryManager, biome, coordinates);
    }

    public int resolveExtendedColor(BlockRenderView world, BlockPos position) {
        yPosition.get().y = position.getY();
        return world.getColor(position, this);
    }

    public IChromaticRegistryResolver getWrappedRegistryManager() {
        return wrappedRegistryManager;
    }

    @Override
    public int getColor(Biome biome, double x, double z) {
        Coordinates coordinates = new Coordinates((int)x, yPosition.get().y, (int)z);
        return 0xffefefe & wrappedRegistryManager.getColorRegistryForDynamicPosition(dynamicRegistryManager, biome, coordinates);
    }

    private static final class YCoordinate {
        int y;
    }
}
