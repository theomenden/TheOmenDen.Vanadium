package vanadium.mixin.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vanadium.customcolors.mapping.BiomeColorMappings;

@Mixin(FluidRenderer.class)
public abstract class FluidRendererMixin {


    @ModifyConstant(
            method = "render",
            constant = @Constant(intValue = 0xFFFFFF)
    )
    private int calculateCustomColor(int original, BlockRenderView world, BlockPos pos, VertexConsumer consumer, BlockState blockState, FluidState fluidState) {
        if(BiomeColorMappings.isFluidCustomColored(fluidState)) {
            var canonicalBlockState = fluidState.getBlockState();

            return BiomeColorMappings.getBiomeColorMapping(canonicalBlockState, world, pos);
        }

        return original;
    }

    @ModifyVariable(
            method = "render",
            at = @At(
                    target = "Lnet/minecraft/client/color/world/BiomeColors;getWaterColor(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I",
                    value = "STORE"
            )
    )
    private int calculateCustomColorVariable(int original, BlockRenderView world, BlockPos pos, VertexConsumer consumer, BlockState blockState, FluidState fluidState) {
        if(BiomeColorMappings.isFluidCustomColored(fluidState)) {
            var canonicalBlockState = fluidState.getBlockState();

            return BiomeColorMappings.getBiomeColorMapping(canonicalBlockState, world, pos);
        }

        return original;
    }

}
