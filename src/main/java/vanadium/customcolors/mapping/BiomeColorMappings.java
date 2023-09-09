package vanadium.customcolors.mapping;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import vanadium.biomeblending.storage.ColorMappingStorage;
import vanadium.customcolors.interfaces.VanadiumResolver;
import vanadium.defaults.DefaultVanadiumResolverProviders;
import vanadium.models.ColorMappingProperties;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public final class BiomeColorMappings {
    private static final ColorMappingStorage<Block> colorMappingsByBlock = new ColorMappingStorage<>(DefaultVanadiumResolverProviders.BLOCK_PROVIDER);
    private static final ColorMappingStorage<BlockState> colorMappingsByState = new ColorMappingStorage<>(DefaultVanadiumResolverProviders.BLOCK_STATE_PROVIDER);
    private static final ColorMappingStorage<FluidState> colorMappingsByFluidState = new ColorMappingStorage<>(DefaultVanadiumResolverProviders.FLUID_STATE_PROVIDER);
    private static final ColorMappingStorage<Fluid> colorMappingsByFluidFog = new ColorMappingStorage<>(DefaultVanadiumResolverProviders.FLUID_FOG_PROVIDER);
    private static final ColorMappingStorage<ResourceLocation> skyFogColorMappings = new ColorMappingStorage<>(DefaultVanadiumResolverProviders.SKY_FOG_PROVIDER);
    private static final ColorMappingStorage<ResourceLocation> skyColorMappings = new ColorMappingStorage<>(DefaultVanadiumResolverProviders.SKY_PROVIDER);

    private BiomeColorMappings() {
    }

    public static VanadiumResolver getTotalSky(ResourceLocation dimensionId) {
        return skyColorMappings.getVanadiumResolver(dimensionId);
    }

    public static VanadiumResolver getTotalSkyFog(ResourceLocation dimensionId) {
        return skyFogColorMappings.getVanadiumResolver(dimensionId);
    }

    public static BiomeColorMapping getFluidFog(RegistryAccess manager, Fluid fluid, Biome biome) {
        return colorMappingsByFluidFog.getColorMapping(manager, fluid, biome);
    }

    public static void addBiomeColorMapping(BiomeColorMapping biomeColorMap) {
        ColorMappingProperties props = biomeColorMap.getProperties();
        Set<ResourceLocation> biomes = props.getApplicableBiomes();
        colorMappingsByState.addColorMapping(biomeColorMap, props.getApplicableBlockStates(), biomes);
        colorMappingsByBlock.addColorMapping(biomeColorMap, props.getApplicableBlocks(), biomes);
        props
                .getApplicableSpecialIds()
                .forEach((key, value) -> {
                    switch (key.toString()) {
                        case "vanadium:sky", "colormatic:sky" -> skyColorMappings.addColorMapping(biomeColorMap, value, biomes);
                        case "vanadium:sky_fog", "colormatic:sky_fog" -> skyFogColorMappings.addColorMapping(biomeColorMap, value, biomes);
                        case "vanadium:fluid_fog", "colormatic:fluid_fog" -> {
                            Collection<Fluid> fluids = value
                                    .stream()
                                    .map(BuiltInRegistries.FLUID::get)
                                    .collect(Collectors.toList());
                            colorMappingsByFluidFog.addColorMapping(biomeColorMap, fluids, biomes);
                        }
                    }
                });
    }

    public static void resetColorMappings() {
        colorMappingsByState.clearMappings();
        colorMappingsByBlock.clearMappings();
        colorMappingsByFluidFog.clearMappings();
        skyColorMappings.clearMappings();
        skyFogColorMappings.clearMappings();
    }

    public static boolean isCustomColored(BlockState state) {
        return colorMappingsByBlock.contains(state.getBlock())
                || colorMappingsByState.contains(state);
    }

    public static boolean isFluidCustomColored(FluidState state) {
        return colorMappingsByFluidFog.contains(state.getType())
                || colorMappingsByFluidState.contains(state);
    }


    public static boolean isItemCustomColored(BlockState state) {
        return colorMappingsByBlock.getFallbackColorMapping(state.getBlock()) != null
                || colorMappingsByState.getFallbackColorMapping(state) != null;
    }

    public static boolean isFluidFogCustomColored(Fluid fluid) {
        return colorMappingsByFluidFog.contains(fluid);
    }

    public static int getBiomeColorMapping(BlockState state, BlockAndTintGetter world, BlockPos pos) {
        if(world != null && pos != null) {
            var resolver = colorMappingsByState.getExtendedResolver(state);
            if(resolver == null) {
                resolver = colorMappingsByBlock.getExtendedResolver(state.getBlock());
            }
            if(resolver == null) {
                throw new IllegalArgumentException(String.valueOf(state));
            }

            return resolver.resolveExtendedColor(world, pos);
        } else {
            BiomeColorMapping colormap = colorMappingsByState.getFallbackColorMapping(state);
            if(colormap == null) {
                colormap = colorMappingsByBlock.getFallbackColorMapping(state.getBlock());
            }
            if(colormap != null) {
                return colormap.getDefaultColor();
            } else {
                return 0xffffff;
            }
        }
    }
}
