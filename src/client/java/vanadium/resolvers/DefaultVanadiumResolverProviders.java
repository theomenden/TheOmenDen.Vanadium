package vanadium.resolvers;

import vanadium.mixin.blocks.BlockColorsAccessor;
import vanadium.models.Coordinates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public record DefaultVanadiumResolverProviders {

    public static final IVanadiumResolverProvider<BlockState> BLOCK_STATE_PROVIDER;
    public static final IVanadiumResolverProvider<BlockState> BLOCK_PROVIDER;
    public static final IVanadiumResolverProvider<BlockState> SKY_PROVIDER;
    public static final IVanadiumResolverProvider<BlockState> SKY_FOG_PROVIDER;
    public static final IVanadiumResolverProvider<BlockState> FLUID_FOG_PROVIDER;


    private static IVanadiumResolver resolveByBlockState(BlockState key) {
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
    private static IVanadiumResolver resolveByBlock(Block key) {
        return resolveByBlockState(key.getDefaultState());
    }

    private static IVanadiumResolver resolveBySky(Identifier key) {
        return (manager, biome, coordinates) -> {
           int color = 0;

           if(Chromatiq.SKY_COLORS.hasCustomColorMapping()
           && key.equals(Chromatiq.OVERWORLD_ID)) {
               color = Chromatiq.SKY_COLORS.getColorMapping.getColor(manager, biome, new Coordinates(coordinates.x(), coordinates.y(), coordinates.y()));
           } else {
               color = Chromatiq.COLOR_PROPERTIES.getProperties().getDimensionSky(key);

               if(color == 0) {
                   color = biome.getSkyColor();
               }
           }
            return color;
        };
    }

    private static IVanadiumResolver resolveByFog(Identifier key) {
        return (manager, biome, coordinates) -> {
            int color = 0;

            if(Chromatiq.FOG_COLORS.hasCustomColorMapping()
            && key.equals(Chromatiq.OVERWORLD_ID)) {
                color = 0xff000000 | Chromatiq.FOG_COLORS.getColorMapping.getColor(manager, biome, (Coordinates)coordinates);
            } else {
                color = Chromatiq.COLOR_PROPERTIES.getProperties().getDimensionFog(key);

                if(color == 0) {
                    color = biome.getFogColor();
                }
            }
            return color;
        };
    }
}
