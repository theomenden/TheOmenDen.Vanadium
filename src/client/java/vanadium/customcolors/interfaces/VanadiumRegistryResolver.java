package vanadium.customcolors.interfaces;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import vanadium.models.Coordinates;

@FunctionalInterface
public interface VanadiumRegistryResolver {
    int getColorRegistryForPosition(DynamicRegistryManager registryManager, Biome biome, Coordinates coordinates);
}
