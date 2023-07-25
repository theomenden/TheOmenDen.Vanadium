package vanadium.resolvers;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import vanadium.models.Coordinates;

@FunctionalInterface
public interface VanadiumRegistryResolver {
    int getColorRegistryForDynamicPosition(RegistryAccess.ImmutableRegistryAccess dynamicRegistryManager, Biome biome, Coordinates coordinates);
}
