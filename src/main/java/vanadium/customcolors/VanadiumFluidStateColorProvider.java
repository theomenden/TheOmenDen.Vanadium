package vanadium.customcolors;

import me.jellysquid.mods.sodium.client.model.color.ColorProvider;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.material.FluidState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vanadium.Vanadium;
import vanadium.customcolors.interfaces.VanadiumResolver;
import vanadium.mixin.sodium.SodiumWorldSliceAccessor;
import vanadium.models.records.Coordinates;

import java.util.Arrays;

public class VanadiumFluidStateColorProvider implements ColorProvider<FluidState> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Vanadium.MODID);
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
        final ClientLevel clientWorld = ((SodiumWorldSliceAccessor)(Object)world).getWorld();
        final RegistryAccess manager = clientWorld.registryAccess();

        int i = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (i == 0) {
            return resolver.getColorAtCoordinatesForBiome(manager, clientWorld.getBiome(pos).value(), new Coordinates(pos.getX(), pos.getY(), pos.getZ()));
        }

        int j = (i * 2 + 1) * (i * 2 + 1);
        int k = 0;
        int l = 0;
        int m = 0;
        Cursor3D cuboidBlockIterator = new Cursor3D(pos.getX() - i, pos.getY(), pos.getZ() - i, pos.getX() + i, pos.getY(), pos.getZ() + i);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        while (cuboidBlockIterator.advance()) {
            mutable.set(cuboidBlockIterator.nextX(), cuboidBlockIterator.nextY(), cuboidBlockIterator.nextZ());
            int n = resolver.getColorAtCoordinatesForBiome(manager, clientWorld.getBiome(mutable).value(), new Coordinates(mutable.getX(), mutable.getY(), mutable.getZ()));
            k += (n & 0xFF0000) >> 16;
            l += (n & 0xFF00) >> 8;
            m += n & 0xFF;
        }
        return (m / j) << 16 | (l / j & 0xFF) << 8 | (k / j)  & 0xFF;
    }
}
