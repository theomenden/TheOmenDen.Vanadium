package vanadium.blending;

import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorSampler;
import me.jellysquid.mods.sodium.client.model.quad.blender.FlatColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.blender.LinearColorBlender;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.State;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import vanadium.resources.DefaultBlockColorSettings;

public class ConfigurableColorBlender implements ColorBlender {
    private final ColorBlender flatBlender;
    private final ColorBlender smoothBlender;

    public ConfigurableColorBlender(MinecraftClient client) {
        this.flatBlender = new FlatColorBlender();
        this.smoothBlender = isSmoothBlendingEnabled(client)
                ? new LinearColorBlender()
                : this;
    }

    @Override
    public <T> int[] getColors(BlockRenderView blockRenderView, BlockPos blockPos, ModelQuadView modelQuadView, ColorSampler<T> colorSampler, T t) {
        return new int[0];
    }

    private static boolean isSmoothBlendingEnabled(State<?,?> state) {
        if(state instanceof BlockState blockState) {
            return DefaultBlockColorSettings.isSmoothBlendingAvailable(blockState.getBlock());
        }

        if(state instanceof FluidState fluidState)  {
            return DefaultBlockColorSettings.isSmoothBlendingAvailable(fluidState.getFluid());
        }

        return false;
    }

    private static boolean isSmoothBlendingEnabled(MinecraftClient client) {
        return client.options.getBiomeBlendRadius().getValue() > 0;
    }
}
