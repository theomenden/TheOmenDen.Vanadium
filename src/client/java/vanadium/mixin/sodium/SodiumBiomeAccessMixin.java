package vanadium.mixin.sodium;

import me.jellysquid.mods.sodium.client.world.biome.BiomeSlice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeSlice.class)
public interface SodiumBiomeAccessMixin {

    @Accessor public long getBiomeSeed();
}
