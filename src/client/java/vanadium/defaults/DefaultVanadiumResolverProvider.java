package vanadium.defaults;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import vanadium.customcolors.interfaces.VanadiumResolver;
import vanadium.customcolors.interfaces.VanadiumResolverProvider;
import vanadium.mixin.coloring.BlockColorsAccessor;

public final class DefaultVanadiumResolverProvider {
    public static final VanadiumResolverProvider<BlockState> BLOCK_STATE = DefaultVanadiumResolverProviders::byBlockState;
    public static final VanadiumResolverProvider<BlockState> BLOCK = DefaultVanadiumResolverProviders::byBlockState;
    public static final VanadiumResolverProvider<BlockState> SKY = DefaultVanadiumResolverProviders::byBlockState;
    public static final VanadiumResolverProvider<BlockState> SKY_FOG = DefaultVanadiumResolverProviders::byBlockState;
    public static final VanadiumResolverProvider<BlockState> FLUID_FOG = key -> (manager, biome, coordinates) -> -1;

    private DefaultVanadiumResolverProvider(){}

    private static VanadiumResolver byBlockState(BlockState key) {
        return (manager, biome, coordinates) -> {
            var colorProvider = ((BlockColorsAccessor) MinecraftClient
                    .getInstance()
                    .getBlockColors())
                    .getProviders()
                    .get(Registries.BLOCK.getRawId(key.getBlock()));

            if (colorProvider != null) {
                var world = MinecraftClient.getInstance().world;
                return colorProvider.getColor(key, world, new BlockPos(coordinates.x(), coordinates.y(), coordinates.z()), 0);
            }

            return -1;
        };
    }
}
