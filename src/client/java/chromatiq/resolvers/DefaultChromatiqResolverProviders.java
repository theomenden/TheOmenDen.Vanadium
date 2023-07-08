package chromatiq.resolvers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3i;

public record DefaultChromatiqResolverProviders {

    public static final ChromatiqResolverProvider<BlockState> BLOCK_STATE_PROVIDER;
    public static final ChromatiqResolverProvider<BlockState> BLOCK_PROVIDER;
    public static final ChromatiqResolverProvider<BlockState> SKY_PROVIDER;
    public static final ChromatiqResolverProvider<BlockState> SKY_FOG_PROVIDER;
    public static final ChromatiqResolverProvider<BlockState> FLUID_FOG_PROVIDER;


    private static ChromatiqResolver resolveByBlockState(BlockState key) {
        return (manager, biome, xPosition, yPosition, zPosition) -> {
            var colorProvider = (BlockColorsAccessor) (MinecraftClient.getInstance()
                    .getBlockColors())
                    .getProviders()
                    .get(Registries.BLOCK.getRawId(key.getBlock()));

            if(colorProvider != null) {
                var world = MinecraftClient.getInstance().world;

                Vec3i pos = new Vec3i((int)xPosition, (int)yPosition, (int)zPosition);
                return colorProvider.getColor(key,
                        world,
                        new BlockPos(pos),
                        0);
            }
            return -1;
        };
    }
    private static ChromatiqResolver resolveByBlock(Block key) {
        return by
    }
}
