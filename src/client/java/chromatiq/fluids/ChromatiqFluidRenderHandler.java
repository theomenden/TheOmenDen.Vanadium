import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

public class ChromatiqFluidRenderHandler implements FluidRenderHandler {
    private final FluidRenderHandler _delegate;

    public ChromatiqFluidRenderHandler(FluidRenderHandler delegate) {
        _delegate = delegate;
    }

    @Override
    public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
        return _delegate.getFluidSprites(view, pos, state);
    }

    @Override
    public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
        var blockState = state.getBlockState();
        if(Biome.isCustomColored(blockState)) {
            return BiomeColormaps.getBiomeColor(blockState, view, pos);
        }
        return _delegate.getFluidColor(view, pos, state);
    }

    @Override
    public void renderFluid(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
        _delegate.renderFluid(pos, world, vertexConsumer, blockState, fluidState);
    }

    @Override
    public void reloadTextures(SpriteAtlasTexture textureAtlas) {
        _delegate.reloadTextures(textureAtlas);
    }
}