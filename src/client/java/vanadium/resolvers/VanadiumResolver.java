package vanadium.resolvers;

import vanadium.models.Coordinates;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;

@FunctionalInterface
public interface VanadiumResolver {
    int getColorAtCoordinatesForBiomeByManager(DynamicRegistryManager manager, Biome biome, Coordinates coordinates);
}
