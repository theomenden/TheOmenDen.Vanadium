package vanadium.blending;

import com.ibm.icu.impl.Utility;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColorResolver;
import org.apache.commons.lang3.Range;
import vanadium.VanadiumBlendingClient;
import vanadium.caching.ColorCache;
import vanadium.models.Coordinates;
import vanadium.util.LinearCongruentialGenerator;
import vanadium.util.MathUtils;

public final class ColorBlending {
    public static final int SAMPLE_X_SEED = 1664525;
    public static final int SAMPLE_Y_SEED = 214013;
    public static final int SAMPLE_Z_SEED = 16807;

    public static final ThreadLocal<BlendingBuffer> threadLocalBlendingBuffer = new ThreadLocal<>();

    public static BlendingBuffer acquireBlendingBuffer(int blendingRadius) {
        BlendingBuffer result;
        BlendingBuffer blendingBuffer = threadLocalBlendingBuffer.get();

        if(blendingBuffer != null
        && blendingBuffer.blendingRadius == blendingRadius) {
            result = blendingBuffer;
        } else {
            result = new BlendingBuffer(blendingRadius);
        }
        result.colorBitsExclusive = 0xFFFFFFFF;
        result.colorBitsInclusive = 0;

        return blendingBuffer;
    }

    public static void releaseBlendingBuffer(BlendingBuffer blendingBuffer) {
        threadLocalBlendingBuffer.set(blendingBuffer);
    }

    public static int getMinimumSliceForBlending(int blendingRadius, int blockSizeLog2, int sliceSizeLog2, int sliceIndex) {
        final int sliceSize = 1 << sliceSizeLog2;
        final int scaledSliceSize = sliceSize >> blockSizeLog2;

        final int scaledBlendingDiameter = (2 * blendingRadius) >> blockSizeLog2;
        final int scaledLowerBlendingRadius = scaledBlendingDiameter - (scaledBlendingDiameter >> 1);

        int result = 0;

        if(sliceIndex == -1) {
            result = scaledSliceSize - scaledLowerBlendingRadius;
        }

        return result;
    }

    public static int getRandomSamplePosition(int minimum, int blockSizeLogBase2, int seed) {
        int blockMask = MathUtils.lowerBitMask(blockSizeLogBase2);

        int seededValue = (minimum ^ seed) + seed;

        int random = LinearCongruentialGenerator.generateRandomNoise(seededValue)
                                                .findFirst()
                                                .getAsInt();

        int offset = random & blockMask;
        return minimum + offset;
    }

    public static void generateColors(World world,
                                      ColorResolver colorResolver,
                                      int colorType,
                                      ColorCache colorCache,
                                      BlendingChunk blendingChunk,
                                      Coordinates coordinates) {
        final int blendingRadius = VanadiumBlendingClient.getBlendingRadiusForBiome();
        if(Range.between(BlendingConfig.BIOME_MINIMUM_BLENDING_RADIUS, BlendingConfig.BIOME_MAXIMUM_BLENDING_RADIUS)
                .contains(blendingRadius)) {
            BlendingBuffer blendingBuffer = acquireBlendingBuffer(blendingRadius);

            gatherColorsIntoBlendingBuffer(world, blendingBuffer, colorResolver, colorType, colorCache, blendingBuffer, coordinates);

            if(blendingBuffer.colorBitsInclusive != blendingBuffer.colorBitsExclusive) {
                blendColorsForSlice(blendingBuffer, blendingChunk, coordinates);
            } else {
                fillBlendingChunkSliceWithColors(blendingChunk, blendingBuffer.colorBitsInclusive, blendingBuffer.sliceSizeLog2, coordinates);
            }

            releaseBlendingBuffer(blendingBuffer);
        } else {
            gatherColorsDirectlyIntoBlendingChunk(world, colorResolver, blendingChunk, coordinates);
        }
    }
}
