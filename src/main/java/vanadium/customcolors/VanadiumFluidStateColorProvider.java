package vanadium.customcolors;

import me.jellysquid.mods.sodium.client.model.color.ColorProvider;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import vanadium.customcolors.interfaces.VanadiumResolver;
import vanadium.customcolors.mapping.BiomeColorMappings;
import vanadium.mixin.sodium.SodiumWorldSliceAccessor;
import vanadium.models.records.Coordinates;

import java.util.Arrays;

public class VanadiumFluidStateColorProvider implements ColorProvider<FluidState> {
    private final VanadiumResolver resolver;

    private VanadiumFluidStateColorProvider(VanadiumResolver resolver) {
        this.resolver = resolver;
    }

    public static VanadiumFluidStateColorProvider adaptVanadiumColorProvider(VanadiumResolver resolver) {
        return new VanadiumFluidStateColorProvider(resolver);
    }

    @Override
    public void getColors(WorldSlice world, BlockPos pos, FluidState state, ModelQuadView quad, int[] output) {
        Arrays.fill(output, resolve(world, pos, state, this.resolver));
    }

    private static int resolve(WorldSlice world, BlockPos pos, FluidState fluidState, VanadiumResolver resolver) {
        if(BiomeColorMappings.isFluidCustomColored(fluidState)) {
            var canonicalBlockState = fluidState.getBlockState();

            return BiomeColorMappings.getBiomeColorMapping(canonicalBlockState, world, pos);
        }

        if(fluidState.isOf(Fluids.WATER)
        || fluidState.isOf(Fluids.FLOWING_WATER)) {
            return world.getColor(pos, BiomeColors.WATER_COLOR);
        }

        var clientWorld = ((SodiumWorldSliceAccessor)(Object)world).getWorld();
        var manager = clientWorld.getRegistryManager();
        var biome = clientWorld.getBiome(pos)
                .value();

        return resolver.getColorAtCoordinatesForBiome(
                manager,
                biome,
                new Coordinates(pos.getX(), pos.getY(), pos.getZ())
        );
    }
}
