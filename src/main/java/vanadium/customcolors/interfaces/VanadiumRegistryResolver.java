package vanadium.customcolors.interfaces;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import vanadium.models.records.Coordinates;

@FunctionalInterface
public interface VanadiumRegistryResolver {
    int getColorRegistryForPosition(RegistryAccess registryManager, Biome biome, Coordinates coordinates);
}
