package vanadium.resolvers;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import vanadium.models.Coordinates;
@FunctionalInterface
public interface VanadiumResolver {
    int getColorAtCoordinatesForBiomeByManager(RegistryAccess manager, Biome biome, Coordinates coordinates);
}
