package vanadium.blending;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorSampler;
import me.jellysquid.mods.sodium.client.model.quad.blender.FlatColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.blender.LinearColorBlender;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.material.FluidState;
import vanadium.resources.DefaultBlockColorSettings;

import javax.swing.plaf.nimbus.State;


public class ConfigurableColorBlender implements ColorBlender {
    private final ColorBlender flatBlender;
    private final ColorBlender smoothBlender;

    public ConfigurableColorBlender(Options clientOptions) {
        this.flatBlender = new FlatColorBlender();
        this.smoothBlender = isSmoothBlendingEnabled(clientOptions)
                ? new LinearColorBlender()
                : this;
    }

    @Override
    public <T> int[] getColors(BlockAndTintGetter blockRenderView, BlockPos blockPos, ModelQuadView modelQuadView, ColorSampler<T> colorSampler, T state) {
        ColorBlender colorBlender;
        if(state instanceof StateHolder<?,?> s && isSmoothBlendingEnabled(s)) {
            colorBlender = this.smoothBlender;
        } else {
            colorBlender = this.flatBlender;
        }

        return colorBlender.getColors(blockRenderView, blockPos, modelQuadView, colorSampler, state);
    }

    private static boolean isSmoothBlendingEnabled(StateHolder<?,?> state) {
        if(state instanceof BlockState blockState) {
            return DefaultBlockColorSettings.isSmoothBlendingAvailable(blockState.getBlock());
        }

        if(state instanceof FluidState fluidState)  {
            return DefaultBlockColorSettings.isSmoothBlendingAvailable(fluidState.getType());
        }

        return false;
    }

    private static boolean isSmoothBlendingEnabled(Options options) {
        return  options.biomeBlendRadius().get().intValue() > 0;
    }
}
