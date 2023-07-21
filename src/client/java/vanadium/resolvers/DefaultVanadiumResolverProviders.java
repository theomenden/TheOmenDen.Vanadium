package vanadium.resolvers;

import net.minecraft.fluid.Fluid;
import vanadium.Vanadium;
import vanadium.mixin.block.BlockColorsAccessor;
import vanadium.models.Coordinates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public final class DefaultVanadiumResolverProviders {

    public static final VanadiumResolverProvider<BlockState> BLOCK_STATE_PROVIDER = DefaultVanadiumResolverProviders::resolveByBlockState;
    public static final VanadiumResolverProvider<Block> BLOCK_PROVIDER = DefaultVanadiumResolverProviders::resolveByBlock;
    public static final VanadiumResolverProvider<Identifier> SKY_PROVIDER = DefaultVanadiumResolverProviders::resolveBySky;
    public static final VanadiumResolverProvider<Identifier> SKY_FOG_PROVIDER = DefaultVanadiumResolverProviders::resolveByFog;
    public static final VanadiumResolverProvider<Fluid> FLUID_FOG_PROVIDER = key -> (manager, biome, coordinates) -> -1;

    private DefaultVanadiumResolverProviders() {

    }

    private static VanadiumResolver resolveByBlockState(BlockState key) {
        return (manager, biome, coordinates) -> {
            var colorProvider = ((BlockColorsAccessor)MinecraftClient.getInstance()
                    .getBlockColors())
                    .getProviders()
                    .get(Registries.BLOCK.getRawId(key.getBlock()));

            if(colorProvider != null) {
                var world = MinecraftClient.getInstance().world;

                Vec3i pos = new Vec3i(coordinates.x(), coordinates.y(), coordinates.z());
                return colorProvider.getColor(key,
                        world,
                        new BlockPos(pos),
                        0);
            }
            return -1;
        };
    }
    private static VanadiumResolver resolveByBlock(Block key) {
        return resolveByBlockState(key.getDefaultState());
    }

    private static VanadiumResolver resolveBySky(Identifier key) {
        return (manager, biome, coordinates) -> {
           int color = 0;

           if(Vanadium.SKY_COLORS.hasCustomColorMapping()
           && key.equals(Vanadium.OVERWORLD_ID)) {
               color = Vanadium.SKY_COLORS.getColorMapping().getColorAtCoordinatesForBiomeByManager(manager, biome, coordinates);
           } else {
               color = Vanadium.COLOR_PROPERTIES.getProperties().getDimensionSky(key);

               if(color == 0) {
                   color = biome.getSkyColor();
               }
           }
            return color;
        };
    }

    private static VanadiumResolver resolveByFog(Identifier key) {
        return (manager, biome, coordinates) -> {
            int color = 0;

            if(Vanadium.FOG_COLORS.hasCustomColorMapping()
            && key.equals(Vanadium.OVERWORLD_ID)) {
                color = 0xff000000 | Vanadium.FOG_COLORS.getColorMapping().getColorAtCoordinatesForBiomeByManager(manager, biome, coordinates);
            } else {
                color = Vanadium.COLOR_PROPERTIES.getProperties().getDimensionFog(key);

                if(color == 0) {
                    color = biome.getFogColor();
                }
            }
            return color;
        };
    }
}
