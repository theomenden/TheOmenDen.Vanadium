package vanadium.colormapping;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import org.apache.commons.lang3.Range;
import vanadium.Vanadium;
import vanadium.models.Coordinates;
import vanadium.models.VanadiumColor;
import vanadium.properties.ColorMappingProperties;
import vanadium.resolvers.ExtendedColorResolver;
import vanadium.resolvers.VanadiumResolver;
import vanadium.util.ColumnBounds;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;

public class BiomeColorMap implements VanadiumResolver {
    private final ColorMappingProperties properties;
    private final NativeImage imageColorMapping;
    private transient final int defaultColor;
    private transient final ExtendedColorResolver resolver;

    public BiomeColorMap(ColorMappingProperties properties, NativeImage colorMapping) {
        this.properties = properties;
        this.imageColorMapping = colorMapping;

        VanadiumColor colorAsHex = properties.getColor();
        if(colorAsHex == null) {
            defaultColor = colorAsHex.rgb();
        } else {
            defaultColor = computeDefaultColor(properties);
        }

        this.resolver = new ExtendedColorResolver(this);
    }

    @Override
    public int getColorAtCoordinatesForBiomeByManager(RegistryAccess manager, Biome biome, Coordinates coordinates) {
        switch(properties.getFormat()) {
            case VANILLA:
                float temp = biome.getBaseTemperature();
                temp = Range.between(0.0F, 1.0F).fit(temp);
                float rain = Range.between(0.0F, 1.0F).fit(biome.climateSettings.downfall());
                return getColorMap(temp, rain);
            case GRID:
                ColumnBounds columnBounds = properties.getColumn(Vanadium.getBiomeKey(manager, biome), manager.registryOrThrow(Registries.BIOME).getResourceKey(biome).orElseThrow());
                @SuppressWarnings({"removal", "deprecation"})
                double fraction = BiomeColors.FOLIAGE_COLOR_RESOLVER.getColor(biome,coordinates.x() * 0.0225, coordinates.z() * 0.0225);
                fraction = (fraction + 1.0) * 0.5;
                int x = columnBounds.Column() + (int)(fraction * columnBounds.Count());
                int y = coordinates.y() - properties.getYOffset();
                int variance = properties.getYVariance();
                RandomGenerator gridRandom = RandomGeneratorFactory.getDefault().create(coordinates.x() * 31L + coordinates.z());
                y += gridRandom.nextInt(variance * 2 + 1) - variance;
                x %= imageColorMapping.getWidth();
                y = Range.between(0, imageColorMapping.getHeight() - 1).fit(y);
                return imageColorMapping.getPixelRGBA(x,y);
            case FIXED:
                return getDefaultColor();
        }
        throw new AssertionError();
    }

    public ColorMappingProperties getProperties() {
        return properties;
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public static int getBiomeCurrentColorOrDefault(BlockAndTintGetter world, BlockPos pos, BiomeColorMap colormap) {
        if(WorldOrPositionIsNull(world, pos)) {
            return colormap.getDefaultColor();
        }
        return colormap.resolver.resolveExtendedColor(world, pos);
    }

    private static boolean WorldOrPositionIsNull(BlockAndTintGetter world, BlockPos pos) {
        return world == null || pos == null;
    }
    
    private int getColorMap(double temperature, double rain) {
        rain *= temperature;
        int x = (int)((1.0D - temperature) * 255.0D);
        int y = (int)((1.0D - rain) * 255.0D);

        if(x >= imageColorMapping.getWidth() || y >= imageColorMapping.getHeight()) {
            return 0xffff00ff;
        }

        return imageColorMapping.getPixelRGBA(x, y);
    }

    private int computeDefaultColor(ColorMappingProperties properties) {
        switch(properties.getFormat()) {
            case VANILLA: return this.imageColorMapping.getPixelRGBA(128,128);
            case GRID:
                try{
                    //wasn't sure if this was the right way to do this, so we're gambling ;)
                    var registry = ResourceKey.createRegistryKey(Registries.BIOME.registry()).cast(Registries.BIOME).get();
                int x = properties.getColumn(Biomes.PLAINS, registry).Column();
                int y = Range.between(0, imageColorMapping.getHeight() - 1).fit(63 - properties.getYOffset());
                return imageColorMapping.getPixelRGBA(x, y);
            } catch(IllegalArgumentException e) {
                    return 0xffffffff;
                }
            case FIXED:
                return 0xffffffff;
        }
        throw new AssertionError("Unknown color mapping format: " + properties.getFormat());
    }

}
