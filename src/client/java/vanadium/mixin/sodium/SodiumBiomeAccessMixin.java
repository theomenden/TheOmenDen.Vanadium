package vanadium.mixin.sodium;

import net.minecraft.world.level.biome.BiomeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeManager.class)
public interface SodiumBiomeAccessMixin {

    @Accessor public long getBiomeZoomSeed();
}
