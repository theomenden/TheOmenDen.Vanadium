package vanadium.customcolors.interfaces;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import vanadium.models.records.Coordinates;


@FunctionalInterface
public interface VanadiumResolver {
    int getColorAtCoordinatesForBiome(RegistryAccess manager, Biome biome, Coordinates coordinates);
}
