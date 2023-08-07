package vanadium.mixin.block;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.entry.Vanadium;
import vanadium.colormapping.BiomeColorMap;
import vanadium.colormapping.BiomeColorMappings;

@Mixin(BlockColors.class)
public abstract class BlockColorsMixin {

    @Dynamic("birch foliage lambda method")
    @Inject(method = "method_1687", at = @At("HEAD"), cancellable = true)
    private static void onBirchColor(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> info) {
        if(Vanadium.BIRCH_COLORS.hasCustomColorMapping()) {
            int color = BiomeColorMap.getBiomeCurrentColorOrDefault(world, pos, Vanadium.BIRCH_COLORS.getColorMapping());
            info.setReturnValue(color);
        }
    }

    @Dynamic("spruce foliage lambda method")
    @Inject(method = "method_1695", at = @At("HEAD"), cancellable = true)
    private static void onSpruceColor(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> info) {
        if(Vanadium.SPRUCE_COLORS.hasCustomColorMapping()) {
            int color = BiomeColorMap
                    .getBiomeCurrentColorOrDefault(world, pos, Vanadium.SPRUCE_COLORS.getColorMapping());
            info.setReturnValue(color);
        }
    }

    @Dynamic("attached stem foliage lambda method")
    @Inject(method = "method_1698", at = @At("HEAD"), cancellable = true)
    private static void onAttachedStemColor(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> info) {
        Block block = state.getBlock();
        if(block == Blocks.ATTACHED_PUMPKIN_STEM && Vanadium.PUMPKIN_STEM_COLORS.hasCustomColorMapping()) {
            info.setReturnValue(Vanadium.PUMPKIN_STEM_COLORS.getColorAtIndex(Integer.MAX_VALUE));
        } else if(block == Blocks.ATTACHED_MELON_STEM && Vanadium.MELON_STEM_COLORS.hasCustomColorMapping()) {
            info.setReturnValue(Vanadium.MELON_STEM_COLORS.getColorAtIndex(Integer.MAX_VALUE));
        }
    }

    @Dynamic("stem foliage lambda method")
    @Inject(method = "method_1696", at = @At("HEAD"), cancellable = true)
    private static void onStemColor(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> info) {
        Block block = state.getBlock();
        if(block == Blocks.PUMPKIN_STEM && Vanadium.PUMPKIN_STEM_COLORS.hasCustomColorMapping()) {
            int age = state.getValue(StemBlock.AGE);
            info.setReturnValue(Vanadium.PUMPKIN_STEM_COLORS.getColorAtIndex(age));
        } else if(block == Blocks.MELON_STEM && Vanadium.MELON_STEM_COLORS.hasCustomColorMapping()) {
            int age = state.getValue(StemBlock.AGE);
            info.setReturnValue(Vanadium.MELON_STEM_COLORS.getColorAtIndex(age));
        }
    }

    @Dynamic("Lily pad lambda method")
    @Inject(method = "method_1684", at = @At("HEAD"), cancellable = true)
    private static void onLilyPadColor(CallbackInfoReturnable<Integer> info) {
        int color = Vanadium.COLOR_PROPERTIES.getProperties().getLilyPad();
        if(color != 0) {
            info.setReturnValue(color);
        }
    }

    @Inject(method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;I)I", at = @At("HEAD"), cancellable = true)
    private void onColorMultiplier(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIdx, CallbackInfoReturnable<Integer> info) {
        // item colors are already taken care of in ItemColorsMixin
        if(world != null && pos != null && BiomeColorMappings.isCustomColored(state)) {
            int color = BiomeColorMappings.getBiomeColorMapping(state, world, pos);
            info.setReturnValue(color);
        }
    }
}
