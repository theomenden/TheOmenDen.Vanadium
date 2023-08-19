package vanadium.customcolors.mapping;

import com.mojang.serialization.Lifecycle;
import lombok.Getter;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.registry.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.apache.commons.lang3.Range;
import vanadium.Vanadium;
import vanadium.customcolors.VanadiumExtendedColorResolver;
import vanadium.customcolors.interfaces.VanadiumResolver;
import vanadium.models.ColorMappingProperties;
import vanadium.models.records.ColumnBounds;
import vanadium.models.records.Coordinates;
import vanadium.models.records.VanadiumColor;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class BiomeColorMapping implements VanadiumResolver {
    @Getter
    private final ColorMappingProperties properties;
    private final NativeImage imageColorMapping;
    @Getter
    private transient final int defaultColor;
    private transient final VanadiumExtendedColorResolver resolver;

    public BiomeColorMapping(ColorMappingProperties properties, NativeImage colorMapping) {
        this.properties = properties;
        this.imageColorMapping = colorMapping;

        VanadiumColor colorAsHex = properties.getColor();

        if(colorAsHex != null) {
            defaultColor = colorAsHex.rgb();
        } else {
           defaultColor = computeDefaultColor(properties);
        }

        this.resolver = new VanadiumExtendedColorResolver(this);
    }

    @Override
    public int getColorAtCoordinatesForBiome(DynamicRegistryManager manager, Biome biome, Coordinates coordinates) {
        switch (properties.getFormat()) {
            case VANILLA -> {
                float temp = biome.getTemperature();
                temp = Range
                        .between(0.0F, 1.0F)
                        .fit(temp);
                float rain = Range
                        .between(0.0F, 1.0F)
                        .fit(biome.weather.downfall());
                return getColorMap(temp, rain);
            }
            case GRID -> {
                ColumnBounds columnBounds = properties.getColumn(Vanadium.getBiomeRegistryKey(manager, biome), manager.get(RegistryKeys.BIOME));
                @SuppressWarnings({"removal", "deprecation"})
                double fraction = Biome.FOLIAGE_NOISE.sample(coordinates.x() * 0.0225, coordinates.z() * 0.0225, false);
                fraction = (fraction + 1.0) * 0.5;
                int x = columnBounds.Column() + (int) (fraction * columnBounds.Count());
                int y = coordinates.y() - properties.getYOffset();
                int variance = properties.getYVariance();
                RandomGenerator gridRandom = RandomGeneratorFactory
                        .getDefault()
                        .create(coordinates.x() * 31L + coordinates.z());
                y += gridRandom.nextInt(variance * 2 + 1) - variance;
                x %= imageColorMapping.getWidth();
                y = Range
                        .between(0, imageColorMapping.getHeight() - 1)
                        .fit(y);
                return imageColorMapping.getColor(x, y);
            }
            case FIXED -> {
                return getDefaultColor();
            }
        }
        throw new AssertionError();
    }

    public static int getBiomeCurrentColorOrDefault(BlockRenderView world, BlockPos pos, BiomeColorMapping colormap) {
        if(WorldOrPositionIsNull(world, pos)) {
            return colormap.getDefaultColor();
        }
        return colormap.resolver.resolveExtendedColor(world, pos);
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

    private int computeDefaultColor(ColorMappingProperties properties) {
        switch (properties.getFormat()) {
            case VANILLA -> {
                return this.imageColorMapping.getColor(128, 128);
            }
            case GRID -> {
                try {
                    var biomeRegistry = new SimpleRegistry<>(
                            RegistryKeys.BIOME,
                            Lifecycle.stable()
                    )
                            .freeze();

                    int x = properties
                            .getColumn(BiomeKeys.PLAINS,
                                    biomeRegistry)
                            .Column();
                    int y = Range
                            .between(0, imageColorMapping.getHeight() - 1)
                            .fit(63 - properties.getYOffset());

                    return imageColorMapping.getColor(x, y);
                } catch (IllegalArgumentException e) {
                    return 0xffffffff;
                }
            }
            case FIXED -> {
                return 0xffffffff;
            }
        }
        throw new AssertionError("Unknown color mapping format: " + properties.getFormat());
    }
}
