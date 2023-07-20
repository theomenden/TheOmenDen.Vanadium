package vanadium.mixin.sodium;

import me.jellysquid.mods.sodium.client.model.quad.blender.ColorSampler;
import me.jellysquid.mods.sodium.client.world.biome.BlockColorsExtended;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import org.spongepowered.asm.mixin.*;
import vanadium.colormapping.BiomeColorMappings;

@Mixin(value= BlockColors.class, priority = 2000)
@Implements(@Interface(iface = BlockColorsExtended.class, prefix ="i$", remap = Interface.Remap.NONE))
public abstract class SodiumBlockColorsMixin implements BlockColorsExtended{
    @Unique
    private static final ColorSampler<BlockState> VANADIUM_PROVIDER =
            (state, world, pos, tintIndex) -> BiomeColorMappings.getBiomeColorMapping(state, world, pos);

    @Intrinsic(displace = true)
    public ColorSampler<BlockState> i$getColorProvider(BlockState state) {
        if(BiomeColorMappings.isCustomColored(state)){
            return VANADIUM_PROVIDER;
        }
        return this.getColorProvider(state);
    }
}
