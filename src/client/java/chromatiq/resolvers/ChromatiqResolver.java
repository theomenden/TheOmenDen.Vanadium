package chromatiq.resolvers;

import chromatiq.models.Coordinates;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;

@FunctionalInterface
public interface ChromatiqResolver {
    int getColorAtCoordinatesForBiomeByManager(DynamicRegistryManager manager, Biome biome, Coordinates coordinates);
}
