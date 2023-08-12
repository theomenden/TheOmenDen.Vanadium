package vanadium.customcolors.interfaces;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import vanadium.models.Coordinates;


@FunctionalInterface
public interface VanadiumResolver {
    int getColorAtCoordinatesForBiome(DynamicRegistryManager manager, Biome biome, Coordinates coordinates);
}
