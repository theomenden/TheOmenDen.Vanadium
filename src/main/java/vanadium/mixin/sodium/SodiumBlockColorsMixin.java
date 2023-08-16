package vanadium.mixin.sodium;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.jellysquid.mods.sodium.client.model.color.interop.BlockColorsExtended;
import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.util.collection.IdList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.customcolors.mapping.BiomeColorMappings;

import java.util.Arrays;

@Mixin(value = BlockColors.class, priority = 2000)
@Implements(
@Interface(
        iface = BlockColorsExtended.class,
        prefix = "i$",
        remap = Interface.Remap.NONE
)
)
public abstract class SodiumBlockColorsMixin implements BlockColorsExtended{
    @Shadow @Final private IdList<BlockColorProvider> providers;
    @Unique
    private final Reference2ReferenceMap<Block, BlockColorProvider> blocksToColor = new Reference2ReferenceOpenHashMap<>();

    private SodiumBlockColorsMixin() {}
    @Unique
    private static final BlockColorProvider VANADIUM_PROVIDER =
            (state, world, pos, tintIndex) -> BiomeColorMappings.getBiomeColorMapping(state, world, pos);


    @Inject(
            method = "registerColorProvider",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preRegisterColorProvider(BlockColorProvider provider, Block[] blocks, CallbackInfo ci) {
        int var5 = blocks.length;

        Arrays
                .stream(blocks, 0, var5)
                .forEach(block -> {
                    if(BiomeColorMappings.isCustomColored(block.getDefaultState())) {
                        this.providers.add(VANADIUM_PROVIDER);
                    }
                    this.providers.add(provider);
                });
        ci.cancel();
    }
}
