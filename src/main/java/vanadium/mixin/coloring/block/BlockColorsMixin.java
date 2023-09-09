package vanadium.mixin.coloring.block;


import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.customcolors.mapping.BiomeColorMapping;
import vanadium.customcolors.mapping.BiomeColorMappings;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(BlockColors.class)
public abstract class BlockColorsMixin {

    @Dynamic("Birch foliage lambda")
    @Inject(method = "method_1687", at = @At("HEAD"), cancellable = true)
    private static void onBirchColoring(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> cir) {
        if(VanadiumColormaticResolution.hasCustomBirchColors()) {
            var birchColors = ObjectUtils.firstNonNull(
                    VanadiumColormaticResolution.BIRCH_COLORS,
                    VanadiumColormaticResolution.COLORMATIC_BIRCH_COLORS
            );
            int color = BiomeColorMapping.getBiomeCurrentColorOrDefault(world, pos, birchColors.getColorMapping());
            cir.setReturnValue(color);
        }
    }

    @Dynamic("Spruce foliage lambda")
    @Inject(method = "method_1695",
    at = @At("HEAD"),
    cancellable = true)
    private static void onSpruceColoring(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> cir) {
        if(VanadiumColormaticResolution.hasCustomSpruceColors()) {
            var spruceColors = ObjectUtils.firstNonNull(
                    VanadiumColormaticResolution.SPRUCE_COLORS,
                    VanadiumColormaticResolution.COLORMATIC_SPRUCE_COLORS
            );
            int color = BiomeColorMapping.getBiomeCurrentColorOrDefault(world, pos, spruceColors.getColorMapping());
            cir.setReturnValue(color);
        }
    }

   @Inject(
    method = "method_1698",
    at = @At("HEAD"),
    cancellable = true)
    private static void onAttachedStemColoring(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIndex, CallbackInfoReturnable<Integer> cir) {
        Block block = state.getBlock();

        if(block == Blocks.ATTACHED_PUMPKIN_STEM &&
                VanadiumColormaticResolution.hasCustomPumpkinStemColors()) {
            var pumpkinStemColors = ObjectUtils.firstNonNull(
                    VanadiumColormaticResolution.PUMPKIN_STEM_COLORS,
                    VanadiumColormaticResolution.COLORMATIC_PUMPKIN_STEM_COLORS);
            cir.setReturnValue(pumpkinStemColors.getColorAtIndex(Integer.MAX_VALUE));
        } else if(block == Blocks.ATTACHED_MELON_STEM
        && VanadiumColormaticResolution.hasCustomMelonStemColors()) {
            var melonStemColors = ObjectUtils.firstNonNull(
                    VanadiumColormaticResolution.MELON_STEM_COLORS,
                    VanadiumColormaticResolution.COLORMATIC_MELON_STEM_COLORS
            );
            cir.setReturnValue(melonStemColors.getColorAtIndex(Integer.MAX_VALUE));
        }
    }

    @Dynamic("stem foliage lambda")
    @Inject(method = "method_1696",
    at = @At("HEAD"),
    cancellable = true)
    private static void onStemColoring(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> cir) {
        Block block = state.getBlock();

        if(block == Blocks.PUMPKIN_STEM
        && VanadiumColormaticResolution.hasCustomPumpkinStemColors()) {
            int age = state.getValue(StemBlock.AGE);

            var pumpkinStemColors = ObjectUtils.firstNonNull(
                    VanadiumColormaticResolution.PUMPKIN_STEM_COLORS,
                    VanadiumColormaticResolution.COLORMATIC_PUMPKIN_STEM_COLORS
            );

            cir.setReturnValue(pumpkinStemColors.getColorAtIndex(age));
        } else if(block == Blocks.MELON_STEM
        && VanadiumColormaticResolution.hasCustomMelonStemColors()) {
            int age = state.getValue(StemBlock.AGE);

            var melonStemColors = ObjectUtils.firstNonNull(
                    VanadiumColormaticResolution.MELON_STEM_COLORS,
                    VanadiumColormaticResolution.COLORMATIC_MELON_STEM_COLORS
            );
            cir.setReturnValue(melonStemColors.getColorAtIndex(age));
        }
    }

    @Dynamic("Lily pad lambda")
    @Inject(method = "method_1684",
    at = @At("HEAD"),
    cancellable = true)
    private static void onLilyPadColoring(CallbackInfoReturnable<Integer> cir) {
        var colorProperties = ObjectUtils.firstNonNull(
                VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                VanadiumColormaticResolution.COLOR_PROPERTIES
        );
        int color = colorProperties.getProperties()
                                             .getLilyPad();

        if(color != 0) {
            cir.setReturnValue(color);
        }
    }

    @Inject(
            method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;I)I",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onColorMultiplier(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> cir) {
        if(world != null
        && pos != null
        && BiomeColorMappings.isCustomColored(state)) {
            int color = BiomeColorMappings.getBiomeColorMapping(state, world, pos);
            cir.setReturnValue(color);
        }
    }
}
