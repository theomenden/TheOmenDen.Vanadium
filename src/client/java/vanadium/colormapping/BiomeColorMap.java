package vanadium.colormapping;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import vanadium.Vanadium;
import vanadium.models.Coordinates;
import vanadium.models.VanadiumColor;
import vanadium.properties.ColorMappingProperties;
import vanadium.resolvers.ExtendedColorResolver;
import vanadium.resolvers.VanadiumRegistryResolver;

import java.util.Random;

public class BiomeColorMap implements VanadiumRegistryResolver {
    private static final Random RANDOMIZED_GRID_LOC = new Random(47L);
    private final ColorMappingProperties properties;
    private final NativeImage imageColorMapping;
    private transient final int defaultColor;
    private transient final ExtendedColorResolver resolver;

    public BiomeColorMap(ColorMappingProperties properties, NativeImage colorMapping) {
        this.properties = properties;
        this.imageColorMapping = colorMapping;

        colorAsHex = properties.getColor();
        if(colorAsHex == null) {
            defaultColor = new VanadiumColor().rgb();
        } else {
            defaultColor = computeDefaultColor(properties);
        }

    }

    @Override
    public int getColorRegistryForDynamicPosition(DynamicRegistryManager dynamicRegistryManager, Biome biome, Coordinates coordinates) {
        return 0;
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public static int getBiomeCurrentColorOrDefault(BlockRenderView world, BlockPos pos, BiomeColorMap colormap) {
        if(WorldOrPositionIsNull(world, pos)) {
            return colormap.getDefaultColor();
        }
        return colormap.resolver.(world, pos);
    }

    private static boolean WorldOrPositionIsNull(BlockRenderView world, BlockPos pos) {
        return world == null || pos == null;
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

}
