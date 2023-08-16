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
            method = "render",
            constant = @Constant(intValue = 0xFFFFFF)
    )
    private int calculateCustomColor(int original, BlockRenderView world, BlockPos pos, VertexConsumer consumer, BlockState blockState, FluidState fluidState) {
        var canonicalBlockState = fluidState.getBlockState();

        if(BiomeColorMappings.isCustomColored(canonicalBlockState)) {
            return BiomeColorMappings.getBiomeColorMapping(canonicalBlockState, world, pos);
        }

        return original;
    }
}
