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
import vanadium.colormapping.BiomeColorMappings;

@Mixin(LiquidBlockRenderer.class)
public class LiquidBlockRendererMixin {
    @ModifyVariable(method="tesselate",
    at = @At("STORE"),
    ordinal = 0)
    private int calculateCustomFluidColor(int original, BlockAndTintGetter world,
                                          BlockPos pos, VertexConsumer vertexConsumer,
                                          BlockState blockState, FluidState fluidState) {
        var canonicalBlockState = fluidState.createLegacyBlock();

        return BiomeColorMappings.isCustomColored(canonicalBlockState)
        ? BiomeColorMappings.getBiomeColorMapping(canonicalBlockState, world, pos)
        : original;
    }


}
