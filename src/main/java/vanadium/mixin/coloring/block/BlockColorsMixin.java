package vanadium.mixin.coloring.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StemBlock;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;
import vanadium.customcolors.mapping.BiomeColorMapping;
import vanadium.customcolors.mapping.BiomeColorMappings;

@Mixin(BlockColors.class)
public abstract class BlockColorsMixin {

    @Dynamic("Birch foliage lambda")
    @Inject(method = "method_1687", at = @At("HEAD"), cancellable = true)
    private static void onBirchColoring(BlockState state, BlockRenderView world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> cir) {
        if(Vanadium.BIRCH_COLORS.hasCustomColorMapping()) {
            int color = BiomeColorMapping.getBiomeCurrentColorOrDefault(world, pos, Vanadium.BIRCH_COLORS.getColorMapping());
            cir.setReturnValue(color);
        }
    }

    @Dynamic("Spruce foliage lambda")
    @Inject(method = "method_1695",
    at = @At("HEAD"),
    cancellable = true)
    private static void onSpruceColoring(BlockState state, BlockRenderView world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> cir) {
        if(Vanadium.SPRUCE_COLORS.hasCustomColorMapping()) {
            int color = BiomeColorMapping.getBiomeCurrentColorOrDefault(world, pos, Vanadium.SPRUCE_COLORS.getColorMapping());
            cir.setReturnValue(color);
        }
    }

    @Dynamic("attached stem foliage lambda")
    @Inject(method = "method_1698",
    at = @At("HEAD"),
    cancellable = true)
    private static void onAttachedStemColoring(BlockState state, BlockRenderView world, Block pos, int tintIndex, CallbackInfoReturnable<Integer> cir) {
        Block block = state.getBlock();

        if(block == Blocks.ATTACHED_PUMPKIN_STEM &&
        Vanadium.PUMPKIN_STEM_COLORS.hasCustomColorMapping()) {
            cir.setReturnValue(Vanadium.PUMPKIN_STEM_COLORS.getColorAtIndex(Integer.MAX_VALUE));
        } else if(block == Blocks.ATTACHED_MELON_STEM
        && Vanadium.MELON_STEM_COLORS.hasCustomColorMapping()) {
            cir.setReturnValue(Vanadium.MELON_STEM_COLORS.getColorAtIndex(Integer.MAX_VALUE));
        }
    }

    @Dynamic("stem foliage lambda")
    @Inject(method = "method_1696",
    at = @At("HEAD"),
    cancellable = true)
    private static void onStemColoring(BlockState state, BlockRenderView world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> cir) {
        Block block = state.getBlock();

        if(block == Blocks.PUMPKIN_STEM
        && Vanadium.PUMPKIN_STEM_COLORS.hasCustomColorMapping()) {
            int age = state.get(StemBlock.AGE);
            cir.setReturnValue(Vanadium.PUMPKIN_STEM_COLORS.getColorAtIndex(age));
        } else if(block == Blocks.MELON_STEM
        && Vanadium.MELON_STEM_COLORS.hasCustomColorMapping()) {
            int age = state.get(StemBlock.AGE);
            cir.setReturnValue(Vanadium.MELON_STEM_COLORS.getColorAtIndex(age));
        }
    }

    @Dynamic("Lily pad lambda")
    @Inject(method = "method_1684",
    at = @At("HEAD"),
    cancellable = true)
    private static void onLilyPadColoring(CallbackInfoReturnable<Integer> cir) {
        int color = Vanadium.COLOR_PROPERTIES.getProperties()
                                             .getLilyPad();

        if(color != 0) {
            cir.setReturnValue(color);
        }
    }

    @Inject(
            method = "getColor",
            at = @At("HEAD"),
            cancellable = true
    )

    private void onColorMultiplier(BlockState state, BlockRenderView world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> cir) {
        if(world != null
        && pos != null
        && BiomeColorMappings.isCustomColored(state)) {
            int color = BiomeColorMappings.getBiomeColorMapping(state, world, pos);
            cir.setReturnValue(color);
        }
    }
}
