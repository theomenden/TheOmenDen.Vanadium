package vanadium.mixin.sodium;

import me.jellysquid.mods.sodium.client.world.WorldSlice;
import me.jellysquid.mods.sodium.client.world.biome.BiomeColorCache;
import me.jellysquid.mods.sodium.client.world.biome.BiomeSlice;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldSlice.class)
public interface SodiumWorldSliceAccessor {
    @Accessor
    BiomeSlice getBiomeSlice();

    @Accessor
    BiomeColorCache getBiomeColors();

    @Accessor
    ClientLevel getWorld();
}
