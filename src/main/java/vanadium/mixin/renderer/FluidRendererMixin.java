package vanadium.mixin.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.customcolors.mapping.BiomeColorMappings;

@Mixin(FluidRenderer.class)
public abstract class FluidRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/world/BiomeColors;getWaterColor(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I", ordinal = 0))
    private void calculateCustomColor(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, CallbackInfo ci ) {
        var canonicalBlockState = fluidState.getBlockState();
        if(BiomeColorMappings.isCustomColored(canonicalBlockState)) {
            BiomeColorMappings.getBiomeColorMapping(canonicalBlockState,world,pos);
        }
    }
}
