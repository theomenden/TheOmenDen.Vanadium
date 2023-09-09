package vanadium.defaults;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
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
    public static final VanadiumResolverProvider<ResourceLocation> SKY_PROVIDER = DefaultVanadiumResolverProviders::bySky;
    public static final VanadiumResolverProvider<ResourceLocation> SKY_FOG_PROVIDER = DefaultVanadiumResolverProviders::byFog;
    public static final VanadiumResolverProvider<Fluid> FLUID_FOG_PROVIDER = key -> (manager, biome, coordinates) -> -1;

    private DefaultVanadiumResolverProviders(){}

    private static VanadiumResolver byBlockState(BlockState key) {
        return (manager, biome, coordinates) -> {
            var colorProvider = ((BlockColorsAccessor) Minecraft
                    .getInstance()
                    .getBlockColors())
                    .getBlockColors()
                    .byId(BuiltInRegistries.BLOCK.getId(key.getBlock()));

            if(colorProvider != null) {
                var world = Minecraft.getInstance().level;
                return colorProvider.getColor(key, world, new BlockPos(coordinates.x(), coordinates.y(), coordinates.z()), 0);
            } else {
                return -1;
            }
        };
    }

    private static VanadiumResolver byFluidState(FluidState key) {
        return byBlockState(key.createLegacyBlock());
    }

    private static VanadiumResolver byBlock(Block key) {
        return byBlockState(key.defaultBlockState());
    }

    private static VanadiumResolver byFluid(Fluid key) {
        return byFluidState(key.defaultFluidState());
    }

    private static VanadiumResolver bySky(ResourceLocation key) {
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
                var colorProperties = ObjectUtils.firstNonNull(
                        VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                        VanadiumColormaticResolution.COLOR_PROPERTIES
                );
                color = colorProperties.getProperties().getDimensionSky(key);

                if(color == 0) {
                    color = biome.getSkyColor();
                }
            }
            return color;
        };
    }

    private static VanadiumResolver byFog(ResourceLocation key) {
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
              var colorProperties = ObjectUtils.firstNonNull(
                      VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                      VanadiumColormaticResolution.COLOR_PROPERTIES
              );
              color = colorProperties.getProperties()
                                               .getDimensionFog(key);

              if(color == 0) {
                  color = biome.getFogColor();
              }
          }
          return color;
        };
    }
}
