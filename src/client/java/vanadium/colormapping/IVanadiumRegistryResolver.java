package vanadium.colormapping;

import vanadium.models.Coordinates;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;

@FunctionalInterface
public interface IVanadiumRegistryResolver {
    int getColorRegistryForDynamicPosition(DynamicRegistryManager dynamicRegistryManager, Biome biome, Coordinates coordinates);
}
