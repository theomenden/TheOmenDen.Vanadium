package vanadium.defaults;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.ObjectUtils;
import vanadium.Vanadium;
import vanadium.customcolors.interfaces.VanadiumResolver;
import vanadium.customcolors.interfaces.VanadiumResolverProvider;
import vanadium.mixin.coloring.BlockColorsAccessor;
import vanadium.utils.VanadiumColormaticResolution;

public final class DefaultVanadiumResolverProviders {
    public static final VanadiumResolverProvider<BlockState> BLOCK_STATE_PROVIDER = DefaultVanadiumResolverProviders::byBlockState;
    public static final VanadiumResolverProvider<FluidState> FLUID_STATE_PROVIDER = DefaultVanadiumResolverProviders::byFluidState;
    public static final VanadiumResolverProvider<Fluid> FLUID_PROVIDER = DefaultVanadiumResolverProviders::byFluid;
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

    private static VanadiumResolver byFluidState(FluidState key) {
        return (manager, biome, coordinates) -> {
            final MinecraftClient client = MinecraftClient.getInstance();
            var colorProvider = ((BlockColorsAccessor) client
                    .getBlockColors())
                    .getProviders()
                    .get(Registries.FLUID.getRawId(key.getFluid()));

            if (colorProvider != null) {
                ClientWorld clientWorld = client.world;
                return colorProvider.getColor(key.getBlockState(), clientWorld, new BlockPos(coordinates.x(), coordinates.y(), coordinates.z()), 0);
            }

            return -1;
        };
    }

    private static VanadiumResolver byBlock(Block key) {
        return byBlockState(key.getDefaultState());
    }

    private static VanadiumResolver byFluid(Fluid key) {
        return byFluidState(key.getDefaultState());
    }

    private static VanadiumResolver bySky(Identifier key) {
        return (manager, biome, coordinates) -> {
            int color;

            if(VanadiumColormaticResolution.hasCustomSkyColors()
            && key.equals(Vanadium.OVERWORLD_ID)) {
                var skyColors = ObjectUtils.firstNonNull(
                        VanadiumColormaticResolution.SKY_COLORS,
                        VanadiumColormaticResolution.COLORMATIC_SKY_COLORS
                );
                color = skyColors
                        .getColorMapping()
                        .getColorAtCoordinatesForBiome(manager, biome, coordinates);
            } else {
                color = Vanadium.COLOR_PROPERTIES.getProperties().getDimensionSky(key);

                if(color == 0) {
                    color = biome.getSkyColor();
                }
            }
            return color;
        };
    }

    private static VanadiumResolver byFog(Identifier key) {
        return (manager, biome, coordinates) -> {
          int color;

          if(VanadiumColormaticResolution.hasCustomFogColors()
          && key.equals(Vanadium.OVERWORLD_ID)) {
              var fogColors = ObjectUtils.firstNonNull(
                      VanadiumColormaticResolution.FOG_COLORS,
                      VanadiumColormaticResolution.COLORMATIC_FOG_COLORS
              );

              color = 0xff000000 | fogColors
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
