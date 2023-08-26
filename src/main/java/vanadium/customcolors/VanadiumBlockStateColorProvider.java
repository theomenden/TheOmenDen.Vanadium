package vanadium.customcolors;

import me.jellysquid.mods.sodium.client.model.color.ColorProvider;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.BlockPos;
import vanadium.customcolors.interfaces.VanadiumResolver;
import vanadium.mixin.sodium.SodiumWorldSliceAccessor;
import vanadium.models.records.Coordinates;

import java.util.Arrays;

public final class VanadiumBlockStateColorProvider implements ColorProvider<BlockState> {
    private final VanadiumResolver resolver;

    private VanadiumBlockStateColorProvider(VanadiumResolver resolver) {
        this.resolver = resolver;
    }

    public static VanadiumBlockStateColorProvider adaptVanadiumColorProvider(VanadiumResolver resolver) {
        return new VanadiumBlockStateColorProvider(resolver);
    }

    @Override
    public void getColors(WorldSlice world, BlockPos pos, BlockState state, ModelQuadView quad, int[] output) {
        Arrays.fill(output, resolve(world, pos, this.resolver));
    }

    private static int resolve(WorldSlice world, BlockPos pos, VanadiumResolver resolver) {
        final ClientWorld clientWorld = ((SodiumWorldSliceAccessor)(Object)world).getWorld();
        final DynamicRegistryManager manager = clientWorld.getRegistryManager().toImmutable();
        int i = MinecraftClient.getInstance().options.getBiomeBlendRadius().getValue();
        if (i == 0) {
            return resolver.getColorAtCoordinatesForBiome(manager, clientWorld.getBiome(pos).value(), new Coordinates(pos.getX(), pos.getY(), pos.getZ()));
        }
        int j = (i * 2 + 1) * (i * 2 + 1);
        int k = 0;
        int l = 0;
        int m = 0;
        CuboidBlockIterator cuboidBlockIterator = new CuboidBlockIterator(pos.getX() - i, pos.getY(), pos.getZ() - i, pos.getX() + i, pos.getY(), pos.getZ() + i);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        while (cuboidBlockIterator.step()) {
            mutable.set(cuboidBlockIterator.getX(), cuboidBlockIterator.getY(), cuboidBlockIterator.getZ());
            int n = resolver.getColorAtCoordinatesForBiome(manager, clientWorld.getBiome(mutable).value(), new Coordinates(mutable.getX(), mutable.getY(), mutable.getZ()));
            k += (n & 0xFF0000) >> 16;
            l += (n & 0xFF00) >> 8;
            m += n & 0xFF;
        }
        return (m / j & 0xF) << 16| (l / j & 0xFF) << 8 |  (k / j & 0xFF);
    }
}