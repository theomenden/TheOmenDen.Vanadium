package vanadium.mixin.sodium;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMaps;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.jellysquid.mods.sodium.client.model.color.interop.BlockColorsExtended;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.IdMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.*;
import vanadium.colormapping.BiomeColorMappings;

@Mixin(value= BlockColors.class, priority = 2000)
@Implements(@Interface(iface = BlockColorsExtended.class, prefix ="i$", remap = Interface.Remap.NONE))
public abstract class SodiumBlockColorsMixin implements BlockColorsExtended{

    @Shadow @Final private IdMapper<BlockColor> blockColors;

    @Unique
    private static final BlockColor VANADIUM_PROVIDER =
            ((blockState, blockAndTintGetter, blockPos, i) -> BiomeColorMappings.getBiomeColorMapping(blockState, blockAndTintGetter, blockPos) );

    @Unique
    private final Reference2ReferenceMap<Block, BlockColor> blocksToVanadium = new Reference2ReferenceOpenHashMap();

    @Intrinsic(displace = true)
    public Reference2ReferenceMap<Block, BlockColor> i$sodium$getProviders() {
        var defaultBlockKey = BuiltInRegistries.BLOCK.getDefaultKey();
        var defaultblock = BuiltInRegistries.BLOCK.get(defaultBlockKey);
        var defaultBlockState = defaultblock.defaultBlockState();

        if(BiomeColorMappings.isCustomColored(defaultBlockState)) {
            return Reference2ReferenceMaps.unmodifiable(this.blocksToVanadium);
        }


        return this.sodium$getProviders();
    }

}
