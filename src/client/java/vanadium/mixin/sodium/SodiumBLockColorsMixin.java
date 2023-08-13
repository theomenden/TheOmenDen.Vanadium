package vanadium.mixin.sodium;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import me.jellysquid.mods.sodium.client.model.color.interop.BlockColorsExtended;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import org.spongepowered.asm.mixin.*;
import vanadium.customcolors.mapping.BiomeColorMappings;

@Mixin(value = BlockColors.class, priority = 2000)
@Implements(
        @Interface(
                iface = BlockColorsExtended.class,
                prefix = "i$",
                remap = Interface.Remap.NONE
        )
)
public abstract class SodiumBLockColorsMixin implements BlockColorsExtended {
    @Unique
    private static final BlockColorProvider VANDIUM_PROVIDER = (state, world, pos, tintIndex) -> BiomeColorMappings.getBiomeColorMapping(state, world, pos);

    @Intrinsic(displace = true)
    public BlockColorProvider i$getColorProviders(BlockState blockState) {
        if(BiomeColorMappings.isCustomColored(state)){
            return new Reference2ReferenceMap<Block, BlockColorProvider>(state.getBlock(),VANDIUM_PROVIDER);
        }
            this.sodium$getProviders();
    }
}
