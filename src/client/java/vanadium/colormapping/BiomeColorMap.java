package vanadium.colormapping;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import vanadium.models.Coordinates;
import vanadium.properties.ColorMappingProperties;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import vanadium.resolvers.VanadiumRegistryResolver;

import java.util.Random;

public class BiomeColorMap implements VanadiumRegistryResolver {
    private static final Random RANDOMIZED_GRID_LOC = new Random(47L);
    private final ColorMappingProperties properties;
    private final NativeImage imageColorMapping;
    private transient final int defaultColor;
    private transient final ExtendChromaticRegistryManager resolver;

    public BiomeColorMap(ColorMappingProperties props, NativeImage colorMapping) {
        properties = props;
        imageColorMapping = colorMapping;
    }

    private static boolean WorldOrPositionIsNull(BlockRenderView world, BlockPos pos) {
        return world == null || pos == null;
    }

    public static int getBiomeCurrentColorOrDefault(BlockRenderView world, BlockPos pos, BiomeColorMap colormap) {
        if(WorldOrPositionIsNull(world, pos)) {
            return colormap.getDefaultColor();
        }
        return colormap.resolver.(world, pos);
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    private int getColorMap(double temperature, double rain) {
        rain *= temperature;
        int x = (int)((1.0D - temperature) * 255.0D);
        int y = (int)((1.0D - rain) * 255.0D);

        if(x >= imageColorMapping.getWidth() || y >= imageColorMapping.getHeight()) {
            return 0xffff00ff;
        }

        return imageColorMapping.getColor(x, y);
    }

    @Override
    public int getColorRegistryForDynamicPosition(DynamicRegistryManager dynamicRegistryManager, Biome biome, Coordinates coordinates) {
        return 0;
    }
}
