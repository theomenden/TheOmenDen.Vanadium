package vanadium.mixin.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import vanadium.customcolors.mapping.BiomeColorMappings;

@Mixin(FluidRenderer.class)
public abstract class FluidRendererMixin {
    @ModifyConstant(
            method="render",
            constant = @Constant(intValue = 16777215)
    )
    private int calculateCustomColor(int original, BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
        int result = original;
        var canonicalBlockState = fluidState.getBlockState();

        if (BiomeColorMappings.isCustomColored(canonicalBlockState)) {
            result = BiomeColorMappings.getBiomeColorMapping(canonicalBlockState, world, pos);
        }
        return result;
    }
}
