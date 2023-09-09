package vanadium.mixin.sodium;

import net.minecraft.world.level.biome.BiomeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeManager.class)
public interface SodiumBiomeAccessAccessor {
    @Accessor
    long getBiomeZoomSeed();
}
