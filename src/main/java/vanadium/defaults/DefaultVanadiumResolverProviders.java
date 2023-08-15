package vanadium.defaults;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import vanadium.Vanadium;
import vanadium.customcolors.interfaces.VanadiumResolver;
import vanadium.customcolors.interfaces.VanadiumResolverProvider;
import vanadium.mixin.coloring.BlockColorsAccessor;

public final class DefaultVanadiumResolverProviders {
    public static final VanadiumResolverProvider<BlockState> BLOCK_STATE_PROVIDER = DefaultVanadiumResolverProviders::byBlockState;
    public static final VanadiumResolverProvider<Block> BLOCK_PROVIDER = DefaultVanadiumResolverProviders::byBlock;
    public static final VanadiumResolverProvider<Identifier> SKY_PROVIDER = DefaultVanadiumResolverProviders::bySky;
    public static final VanadiumResolverProvider<Identifier> SKY_FOG_PROVIDER = DefaultVanadiumResolverProviders::byFog;
    public static final VanadiumResolverProvider<Fluid> FLUID_FOG_PROVIDER = key -> (manager, biome, coordinates) -> -1;

    private DefaultVanadiumResolverProviders(){}

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

    private static VanadiumResolver byBlock(Block key) {
        return byBlockState(key.getDefaultState());
    }

    private static VanadiumResolver bySky(Identifier key) {
        return (manager, biome, coordinates) -> {
            int color;

            if (!Vanadium.SKY_COLORS.hasCustomColorMapping()
                    || !key.equals(Vanadium.OVERWORLD_ID)) {
                color = Vanadium.COLOR_PROPERTIES
                        .getProperties()
                        .getDimensionSky(key);

                if (color != 0) {
                    return color;
                }
                color = biome.getSkyColor();
            } else {
                color = Vanadium.SKY_COLORS
                        .getColorMapping()
                        .getColorAtCoordinatesForBiome(manager, biome, coordinates);
            }
            return color;
        };
    }

    private static VanadiumResolver byFog(Identifier key) {
        return (manager, biome, coordinates) -> {
          int color;

          if(Vanadium.FOG_COLORS.hasCustomColorMapping()
          && key.equals(Vanadium.OVERWORLD_ID)) {
              color = 0xff000000 | Vanadium.FOG_COLORS
                      .getColorMapping()
                      .getColorAtCoordinatesForBiome(manager, biome, coordinates);
          } else {
              color = Vanadium.COLOR_PROPERTIES.getProperties()
                                               .getDimensionFog(key);

              if(color == 0) {
                  color = biome.getFogColor();
              }
          }
          return color;
        };
    }
}
