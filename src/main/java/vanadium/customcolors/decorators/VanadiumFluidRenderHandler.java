package vanadium.customcolors.decorators;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import vanadium.customcolors.mapping.BiomeColorMappings;

public class VanadiumFluidRenderHandler implements FluidRenderHandler {
    private final FluidRenderHandler delegate;

    public VanadiumFluidRenderHandler(FluidRenderHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public TextureAtlasSprite[] getFluidSprites(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
        return this.delegate.getFluidSprites(view, pos, state);
    }

    @Override
    public int getFluidColor(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
        var blockState = state.createLegacyBlock();
        if(BiomeColorMappings.isCustomColored(blockState)) {
            return BiomeColorMappings.getBiomeColorMapping(blockState, view, pos);
        }
        return this.delegate.getFluidColor(view, pos, state);
    }

    @Override
    public void renderFluid(BlockPos pos, BlockAndTintGetter world, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
        this.delegate.renderFluid(pos, world, vertexConsumer, blockState, fluidState);
    }

    @Override
    public void reloadTextures(TextureAtlas textureAtlas) {
        this.delegate.reloadTextures(textureAtlas);
    }
}
