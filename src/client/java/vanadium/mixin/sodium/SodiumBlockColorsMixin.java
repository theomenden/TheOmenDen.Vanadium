package vanadium.mixin.sodium;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMaps;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.jellysquid.mods.sodium.client.model.color.interop.BlockColorsExtended;
import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.customcolors.mapping.BiomeColorMappings;

import java.util.Arrays;
import java.util.Set;

@Mixin(value = BlockColors.class, priority = 2000)
@Implements(
        @Interface(
                iface = BlockColorsExtended.class,
                prefix = "i$",
                remap = Interface.Remap.NONE
        )
)
public abstract class SodiumBlockColorsMixin implements BlockColorsExtended {


    @Shadow public abstract Set<Property<?>> getProperties(Block block);

    @Unique
    private static final BlockColorProvider VANADIUM_PROVIDER =
            (state, world, pos, tintIndex) -> BiomeColorMappings.getBiomeColorMapping(state, world, pos);

    @Unique
    private final Reference2ReferenceMap<Block, BlockColorProvider> blocksToColor = new Reference2ReferenceOpenHashMap<>();
    @Inject(
            method = "registerColorProvider",
            at = @At("HEAD")
    )
    private void onPreRegisterColorProvider(BlockColorProvider provider, Block[] blocks, CallbackInfo ci) {
        int blocksLength = blocks.length;

        Arrays
                .stream(blocks)
                .forEach(block -> {
                    if (BiomeColorMappings.isCustomColored(block.getDefaultState())) {
                        this.blocksToColor.put(block, VANADIUM_PROVIDER);
                        return;
                    }
                    this.blocksToColor.put(block, provider);
                });

    }

    @Intrinsic(displace = true)
    public Reference2ReferenceMap<Block, BlockColorProvider> i$sodium$getProviders() {
            return Reference2ReferenceMaps.unmodifiable(this.blocksToColor);
    }
}
