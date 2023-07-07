package chromatiq.colormapping;

import chromatiq.models.Coordinates;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;

public final class ExtendedChromaticRegistryManager implements IChromaticRegistryManager{

    private static DynamicRegistryManager dynamicRegistryManager;

    private final ThreadLocal<YCoordinate> yPosition;
    private final IChromaticRegistryManager wrappedRegistryManager;

    @Override
    public int getColorRegistryForDynamicPosition(DynamicRegistryManager dynamicRegistryManager, Biome biome, Coordinates coordinates) {
        return 0;
    }

    public IChromaticRegistryManager getWrappedRegistryManager() {
        return wrappedRegistryManager;
    }

    private static final class YCoordinate {
        int y;
    }
}
