package vanadium.mixin.sodium;

import net.minecraft.world.biome.source.BiomeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeAccess.class)
public interface SodiumBiomeAccessAccessor {
    @Accessor
    long getSeed();
}
