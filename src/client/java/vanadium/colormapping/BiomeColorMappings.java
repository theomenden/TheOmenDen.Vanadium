package vanadium.colormapping;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionDefaults;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import org.intellij.lang.annotations.Identifier;
import vanadium.properties.ColorMappingProperties;
import vanadium.resolvers.DefaultVanadiumResolverProviders;
import vanadium.resolvers.VanadiumResolver;
import vanadium.util.ColorMappingStorage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class BiomeColorMappings {

    private static final ColorMappingStorage<MapColor> colorMappingsByBlock = new ColorMappingStorage<>(DefaultVanadiumResolverProviders.BLOCK_PROVIDER);
    private static final ColorMappingStorage<BlockState> colorMappingsByState = new ColorMappingStorage<>(DefaultVanadiumResolverProviders.BLOCK_STATE_PROVIDER);
    private static final ColorMappingStorage<MapColor> colorMappingsByFluidFog = new ColorMappingStorage<>(DefaultVanadiumResolverProviders.FLUID_FOG_PROVIDER);
    private static final ColorMappingStorage<ResourceLocation> skyFogColorMappings = new ColorMappingStorage<>(DefaultVanadiumResolverProviders.SKY_FOG_PROVIDER);
    private static final ColorMappingStorage<ResourceLocation> skyColorMappings = new ColorMappingStorage<>(DefaultVanadiumResolverProviders.SKY_PROVIDER);

    private BiomeColorMappings() {
    }

    public static VanadiumResolver getTotalSky(ResourceKey<DimensionType> dimensionId) {
        return skyColorMappings.getVanadiumResolver(dimensionId.registry());
    }

    public static VanadiumResolver getTotalSkyFog(ResourceLocation dimensionId) {
        return skyFogColorMappings.getVanadiumResolver(dimensionId);
    }

    public static BiomeColorMap getFluidFog(DynamicRegistryManager manager, Fluid fluid, Biome biome) {
        return colorMappingsByFluidFog.getBiomeColorMapping(manager, fluid, biome);
    }

    public static void addBiomeColorMapping(BiomeColorMap biomeColorMap) {
        ColorMappingProperties properties = biomeColorMap.getProperties();
        Set<Identifier> biomes = properties.getApplicableBiomes();
        colorMappingsByState.addColorMapping(biomeColorMap, properties.getApplicableBlockStates(), biomes);
        colorMappingsByBlock.addColorMapping(biomeColorMap, properties.getApplicableBlocks(), biomes);

        properties
                .getApplicableSpecialIds()
                .entrySet()
                .forEach(entry -> {
                    switch (entry
                            .getKey()
                            .toString()) {
                        case "vanadium:sky", "colormatic:sky" ->
                                skyColorMappings.addColorMapping(biomeColorMap, entry.getValue(), biomes);
                        case "vanadium:sky_fog", "colormatic:sky_fog" ->
                                skyFogColorMappings.addColorMapping(biomeColorMap, entry.getValue(), biomes);
                        case "vanadium:fluid_fog", "colormatic:fluid_fog" -> {
                            Collection<Fluid> fluids = entry
                                    .getValue()
                                    .stream()
                                    .map(Registries.FLUID::get)
                                    .collect(Collectors.toList());
                            colorMappingsByFluidFog.addColorMapping(biomeColorMap, fluids, biomes);
                        }
                    }
                });
    }

    public static void resetColorMappings() {
        colorMappingsByState.clearFields();
        colorMappingsByBlock.clearFields();
        colorMappingsByFluidFog.clearFields();
        skyColorMappings.clearFields();
        skyFogColorMappings.clearFields();
    }

    public static boolean isCustomColored(BlockState state) {
        return colorMappingsByBlock.containsColorMapping(state.getBlock().defaultMapColor())
                || colorMappingsByState.containsColorMapping(state);
    }

    public static boolean isItemCustomColored(BlockState state) {
        return colorMappingsByBlock.getFallbackColorMap(state.getBlock()) != null
                || colorMappingsByState.getFallbackColorMap(state) != null;
    }

    public static boolean isFluidFogCustomColored(Fluid fluid) {
        return colorMappingsByFluidFog.containsColorMapping(fluid.defaultFluidState());
    }

    public static int getBiomeColorMapping(BlockState state, BlockAndTintGetter world, BlockPos pos) {
        if(world != null && pos != null) {
            var resolver = colorMappingsByState.getResolver(state);

            if(resolver == null) {
                throw new IllegalArgumentException(String.valueOf(state));
            }

            return resolver.resolveExtendedColor(world, pos);
        }

        BiomeColorMap biomeColorMap = colorMappingsByState.getFallbackColorMap(state);

        if(biomeColorMap == null) {
            biomeColorMap = colorMappingsByBlock.getFallbackColorMap(state.getMapColor(world, pos));
        }

        if(biomeColorMap != null) {
            return biomeColorMap.getDefaultColor();
        }

        return 0xffffff;
    }
}
