package vanadium.customcolors;

import me.jellysquid.mods.sodium.client.model.color.ColorProvider;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import vanadium.mixin.sodium.SodiumWorldSliceAccessor;

import java.util.Arrays;

public final class VanadiumBlockStateColorProvider implements ColorProvider<BlockState> {
    private final VanadiumExtendedColorResolver resolver;

    private VanadiumBlockStateColorProvider(VanadiumExtendedColorResolver resolver) {
        this.resolver = resolver;
    }

    public static VanadiumBlockStateColorProvider adaptVanadiumColorProvider(VanadiumExtendedColorResolver resolver) {
        return new VanadiumBlockStateColorProvider(resolver);
    }

    @Override
    public void getColors(WorldSlice world, BlockPos pos, BlockState state, ModelQuadView quad, int[] output) {
        Arrays.fill(output, resolve(world, pos, this.resolver));
    }

    private static int resolve(WorldSlice world, BlockPos pos, VanadiumExtendedColorResolver resolver) {
        try{
            var color = resolver.resolveExtendedColor(world, pos);
            return color;
        } catch (Exception e) {
            return resolver.getColor(((SodiumWorldSliceAccessor)(Object)world).getBiomeSlice()
                                                                              .getBiome(pos.getX(), pos.getY(), pos.getZ()),pos.getX(), pos.getZ());
        }
    }
}