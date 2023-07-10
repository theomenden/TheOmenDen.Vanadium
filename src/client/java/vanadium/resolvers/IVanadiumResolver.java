package vanadium.resolvers;

import vanadium.models.Coordinates;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;

@FunctionalInterface
public interface IVanadiumResolver {
    int getColorAtCoordinatesForBiomeByManager(DynamicRegistryManager manager, Biome biome, Coordinates coordinates);
}
