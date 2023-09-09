package vanadium.mixin.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vanadium.customcolors.mapping.BiomeColorMappings;

@Mixin(LiquidBlockRenderer.class)
public abstract class FluidRendererMixin {
    @ModifyVariable(
            method = "tesselate(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V",
            at = @At(value = "STORE"),
            ordinal = 0
    )
    private int calculateFluidColor(int original, BlockAndTintGetter level, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
        var canonicalBlockState = fluidState.createLegacyBlock();
        if(BiomeColorMappings.isCustomColored(canonicalBlockState)) {
            return BiomeColorMappings.getBiomeColorMapping(canonicalBlockState, level, pos);
        }
        return original;
    }

}
