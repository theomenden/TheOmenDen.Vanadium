package vanadium.blending;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import vanadium.configuration.VanadiumBlendingConfiguration;
import vanadium.caching.ColorBlendingCache;
import vanadium.caching.ColorCache;
import vanadium.caching.ColorSlice;
import vanadium.models.Coordinates;
import vanadium.util.ColorConverter;
import vanadium.util.LinearCongruentialGenerator;
import vanadium.util.MathUtils;

import java.util.Arrays;

public final class ColorBlending {
    public static final int SAMPLE_SEED_X = 1664525;
    public static final int SAMPLE_SEED_Y = 214013;
    public static final int SAMPLE_SEED_Z = 16807;

    public static final ThreadLocal<BlendingBuffer> threadLocalBlendBuffer = new ThreadLocal<>();

    public static BlendingBuffer acquireBlendBuffer(int blendRadius)
    {
        BlendingBuffer result;
        BlendingBuffer buffer = threadLocalBlendBuffer.get();

        if (buffer != null && buffer.blendingRadius == blendRadius)
        {
            result = buffer;
        }
        else
        {
            result = new BlendingBuffer(blendRadius);
        }

        result.colorBitsExclusive = 0xFFFFFFFF;
        result.colorBitsInclusive = 0;

        return result;
    }

    public static void
    releaseBlendBuffer(BlendingBuffer buffer)
    {
        threadLocalBlendBuffer.set(buffer);
    }

    public static int
    getSliceMin(int blendRadius, int blockSizeLog2, int sliceSizeLog2, int sliceIndex)
    {
        final int sliceSize = shiftLogrithmicSliceSize(sliceSizeLog2);
        final int scaledSliceSize = resolveScaledResults(blockSizeLog2, sliceSize);

        final int scaledBlendDiameter = resolveScaledResults(blockSizeLog2, (2 * blendRadius));
        final int scaledLowerBlendRadius = scaledBlendDiameter - (scaledBlendDiameter >> 1);

        int result = 0;

        if (sliceIndex == -1)
        {
            result = scaledSliceSize - scaledLowerBlendRadius;
        }

        return result;
    }

    private static int resolveScaledResults(int blockSizeLog2, int sliceSize) {
        final int scaledSliceSize = sliceSize >> blockSizeLog2;
        return scaledSliceSize;
    }

    private static int shiftLogrithmicSliceSize(int sliceSizeLog2) {
        final int sliceSize       = 1 << sliceSizeLog2;
        return sliceSize;
    }

    public static int
    getSliceMax(int blendRadius, int blockSizeLog2, int sliceSizeLog2, int sliceIndex)
    {
        final int sliceSize = shiftLogrithmicSliceSize(sliceSizeLog2);
        final int scaledSliceSize = resolveScaledResults(blockSizeLog2, sliceSize);

        final int scaledBlendDiameter = resolveScaledResults(blockSizeLog2, (2 * blendRadius));
        final int scaledUpperBlendRadius = resolveScaledResults(1, scaledBlendDiameter);

        int result = scaledSliceSize;

        if (sliceIndex == 1)
        {
            result = scaledUpperBlendRadius;
        }

        return result;
    }

    public static int
    getBlendMin(int blendRadius, int blockSizeLog2, int sliceSizeLog2, int sliceIndex)
    {
        final int sliceSize = shiftLogrithmicSliceSize(sliceSizeLog2);
        final int scaledSliceSize = resolveScaledResults(blockSizeLog2, sliceSize);

        final int scaledBlendDiameter = resolveScaledResults(blockSizeLog2, (2 * blendRadius));
        final int scaledLowerBlendRadius = scaledBlendDiameter - (scaledBlendDiameter >> 1);

        int result = 0;

        if (sliceIndex >= 0)
        {
            result += scaledLowerBlendRadius;

            if (sliceIndex == 1)
            {
                result += scaledSliceSize;
            }
        }

        return result;
    }

    public static Biome getDefaultBiome(ClientWorld world)
    {
        return world.getRegistryManager().get(RegistryKeys.BIOME).getOrThrow(BiomeKeys.PLAINS);
    }

    public static Biome getBiomeAtPositionOrDefault(World world, BlockPos blockPosition)
    {
        return world.getBiomeAccess().getBiome(blockPosition).value();
    }

    public static Biome  getBiomeAtPositionOrDefaultOrThrow(World world, BlockPos blockPos)
    {
        Biome result = getBiomeAtPositionOrDefault(world, blockPos);

        if (result == null)
        {
            throw new IllegalStateException("Biome could not be retrieved for block position.");
        }

        return result;
    }

    public static int
    getColorAtPosition(World world, BlockPos blockPos, float posX, float posZ, ColorResolver colorResolver)
    {
        Biome biome = getBiomeAtPositionOrDefaultOrThrow(world, blockPos);

        int result = colorResolver.getColor(biome, posX, posZ);

        return result;
    }

    public static int getRandomSamplePosition(int min, int blockSizeLog2, int seed)
    {
        int blockMask = MathUtils.createLowerBitMask(blockSizeLog2);

        int random = LinearCongruentialGenerator.generateRandomNoise(seed).findFirst().getAsInt();
        int offset = random & blockMask;
        int result = min + offset;

        return result;
    }

    public static void gatherColorsForSlice(
            World         world,
            ColorResolver colorResolver,
            ColorSlice colorSlice,
            BlendingBuffer blendBuffer,
            int           sliceIDX,
            int           sliceIDY,
            int           sliceIDZ,
            int           sliceX,
            int           sliceY,
            int           sliceZ)
    {
        BlockPos.Mutable blockPos = new BlockPos.Mutable();

        final int blendRadius = blendBuffer.blendingRadius;

        final int sliceSizeLog2 = blendBuffer.sliceSizeLog2;
        final int blockSizeLog2 = blendBuffer.blockSizeLog2;

        final int sliceSize = blendBuffer.sliceSize;
        final int blendSize = blendBuffer.blendingBufferSize;

        final int sliceMinX = getSliceMin(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDX);
        final int sliceMinY = getSliceMin(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDY);
        final int sliceMinZ = getSliceMin(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDZ);

        final int sliceMaxX = getSliceMax(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDX);
        final int sliceMaxY = getSliceMax(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDY);
        final int sliceMaxZ = getSliceMax(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDZ);

        final int blendMinX = getBlendMin(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDX);
        final int blendMinY = getBlendMin(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDY);
        final int blendMinZ = getBlendMin(blendRadius, blockSizeLog2, sliceSizeLog2, sliceIDZ);

        final int dimX = sliceMaxX - sliceMinX;
        final int dimY = sliceMaxY - sliceMinY;
        final int dimZ = sliceMaxZ - sliceMinZ;

        int worldMinX = (sliceX << sliceSizeLog2) + (sliceMinX << blockSizeLog2);
        int worldMinY = (sliceY << sliceSizeLog2) + (sliceMinY << blockSizeLog2);
        int worldMinZ = (sliceZ << sliceSizeLog2) + (sliceMinZ << blockSizeLog2);

        if ((blendBuffer.scaledBlendingDiameter & 1) != 0 && blockSizeLog2 > 0)
        {
            worldMinX += (1 << (blockSizeLog2 - 1));
            worldMinY += (1 << (blockSizeLog2 - 1));
            worldMinZ += (1 << (blockSizeLog2 - 1));
        }

        int sliceIndexZ =     ColorBlendingCache.getArrayIndex(sliceSize, sliceMinX, sliceMinY, sliceMinZ);
        int blendIndexZ = 3 * ColorBlendingCache.getArrayIndex(blendSize, blendMinX, blendMinY, blendMinZ);

        for (int z = 0;
             z < dimZ;
             ++z)
        {
            int sliceIndexY = sliceIndexZ;
            int blendIndexY = blendIndexZ;

            for (int y = 0;
                 y < dimY;
                 ++y)
            {
                int sliceIndex = sliceIndexY;
                int blendIndex = blendIndexY;

                for (int x = 0;
                     x < dimX;
                     ++x)
                {
                    int cachedColor = colorSlice.data[sliceIndex];

                    if (cachedColor == 0)
                    {
                        final int sampleMinX = worldMinX + (x << blockSizeLog2);
                        final int sampleMinY = worldMinY + (y << blockSizeLog2);
                        final int sampleMinZ = worldMinZ + (z << blockSizeLog2);

                        final int sampleX = getRandomSamplePosition(sampleMinX, blockSizeLog2, SAMPLE_SEED_X);
                        final int sampleY = getRandomSamplePosition(sampleMinY, blockSizeLog2, SAMPLE_SEED_Y);;
                        final int sampleZ = getRandomSamplePosition(sampleMinZ, blockSizeLog2, SAMPLE_SEED_Z);;

                        blockPos.set(sampleX, sampleY, sampleZ);

                        cachedColor = getColorAtPosition(world, blockPos, sampleX, sampleZ, colorResolver);

                        colorSlice.data[sliceIndex] = cachedColor;
                    }

                    float[] calculatedOkLabs = ColorConverter.convertSrgbToOkLabAsFloatArray(cachedColor);

                    System.arraycopy(calculatedOkLabs, 0, blendBuffer.color, 0, calculatedOkLabs.length);


                    blendBuffer.color[blendIndexY] = calculatedOkLabs[0];

                    blendBuffer.colorBitsExclusive &= cachedColor;
                    blendBuffer.colorBitsInclusive |= cachedColor;

                    sliceIndex += 1;
                    blendIndex += 3;
                }

                sliceIndexY += sliceSize;
                blendIndexY += 3 * blendSize;
            }

            sliceIndexZ += sliceSize * sliceSize;
            blendIndexZ += 3 * blendSize * blendSize;
        }
    }

    private static void setColorBitsToCenterColor(
            World         world,
            ColorResolver colorResolver,
            BlendingBuffer   blendBuffer,
            int           sliceX,
            int           sliceY,
            int           sliceZ)
    {
        int centerX = (sliceX << blendBuffer.sliceSizeLog2) + (1 << (blendBuffer.sliceSizeLog2 - 1));
        int centerY = (sliceY << blendBuffer.sliceSizeLog2) + (1 << (blendBuffer.sliceSizeLog2 - 1));
        int centerZ = (sliceZ << blendBuffer.sliceSizeLog2) + (1 << (blendBuffer.sliceSizeLog2 - 1));

        BlockPos blockPos = new BlockPos(centerX, centerY, centerZ);

        int color = getColorAtPosition(world, blockPos, centerX, centerZ, colorResolver);

        blendBuffer.colorBitsInclusive = color;
        blendBuffer.colorBitsExclusive = color;
    }

    public static boolean neighborChunksAreLoaded(
            World   world,
            int           sliceSizeLog2,
            int           sliceX,
            int           sliceZ)
    {
        boolean result = true;

        int prevChunkX = Integer.MAX_VALUE;
        int prevChunkZ = Integer.MAX_VALUE;

        for (int sliceOffsetZ = -1;
             sliceOffsetZ <= 1;
             ++sliceOffsetZ)
        {
            int neighborSliceZ = sliceZ + sliceOffsetZ;
            int neighborChunkZ = neighborSliceZ >> (4 - sliceSizeLog2);

            if (neighborChunkZ != prevChunkZ)
            {
                for (int sliceOffsetX = -1;
                     sliceOffsetX <= 1;
                     ++sliceOffsetX)
                {
                    int neighborSliceX = sliceX + sliceOffsetX;
                    int neighborChunkX = neighborSliceX >> (4 - sliceSizeLog2);

                    if (neighborChunkX != prevChunkX)
                    {
                        Chunk chunk = world.getChunk(neighborChunkX, neighborChunkZ, ChunkStatus.BIOMES, false);

                        if (chunk == null)
                        {
                            result = false;
                            break;
                        }
                    }

                    prevChunkX = neighborChunkX;
                }
            }

            prevChunkZ = neighborChunkZ;
        }

        return result;
    }

    public static void gatherColorsToBlendBuffer(
            World         world,
            ColorResolver colorResolver,
            int           colorType,
            ColorCache    colorCache,
            BlendingBuffer   blendBuffer,
            int           x,
            int           y,
            int           z)
    {
        final int sliceX = x >> blendBuffer.sliceSizeLog2;
        final int sliceY = y >> blendBuffer.sliceSizeLog2;
        final int sliceZ = z >> blendBuffer.sliceSizeLog2;

        boolean neighborsAreLoaded = neighborChunksAreLoaded(world, blendBuffer.sliceSizeLog2, sliceX, sliceZ);

        if (neighborsAreLoaded)
        {
            boolean[] finishedSlices = new boolean[27];

            final int iterationCount = 2;

            for (int iteration = 0;
                 iteration < 2;
                 ++iteration)
            {
                boolean lastIteration    = ((iteration + 1) == iterationCount);
                boolean tryLock          = !lastIteration;
                boolean hasMissingSlices = false;
                int     sliceIndex       = 0;

                for (int sliceOffsetZ = -1;
                     sliceOffsetZ <= 1;
                     ++sliceOffsetZ)
                {
                    for (int sliceOffsetY = -1;
                         sliceOffsetY <= 1;
                         ++sliceOffsetY)
                    {
                        for (int sliceOffsetX = -1;
                             sliceOffsetX <= 1;
                             ++sliceOffsetX)
                        {
                            if (!finishedSlices[sliceIndex])
                            {
                                final int neighborSliceX = sliceX + sliceOffsetX;
                                final int neighborSliceY = sliceY + sliceOffsetY;
                                final int neighborSliceZ = sliceZ + sliceOffsetZ;

                                ColorSlice colorSlice = colorCache.getOrInitSliceByCoordinates(blendBuffer.sliceSize, neighborSliceX, neighborSliceY, neighborSliceZ, colorType, tryLock);

                                if (colorSlice != null)
                                {
                                    gatherColorsForSlice(
                                            world,
                                            colorResolver,
                                            colorSlice,
                                            blendBuffer,
                                            sliceOffsetX,
                                            sliceOffsetY,
                                            sliceOffsetZ,
                                            neighborSliceX,
                                            neighborSliceY,
                                            neighborSliceZ);

                                    colorCache.releaseSliceFromCache(colorSlice);

                                    finishedSlices[sliceIndex] = true;
                                }
                                else
                                {
                                    hasMissingSlices = true;
                                }
                            }

                            ++sliceIndex;
                        }
                    }
                }

                if (!hasMissingSlices)
                {
                    break;
                }
            }
        }
        else
        {
            setColorBitsToCenterColor(
                    world,
                    colorResolver,
                    blendBuffer,
                    sliceX,
                    sliceY,
                    sliceZ);
        }
    }

    public static void blendColorsForSlice(BlendingBuffer buffer, BlendingChunk blendChunk, int inputX, int inputY, int inputZ)
    {
        final int srcSize = BlendingConfig.getBlendingSize(buffer.blendingRadius);
        final int dstSize = BlendingConfig.getSliceSize(buffer.blendingRadius);

        final int blendBufferDim = BlendingConfig.getBlendingBufferSize(buffer.blendingRadius);

        final int filterSupport = BlendingConfig.getFilterSupport(buffer.blendingRadius);
        final int fullFilterDim = filterSupport - 1;
        final int scaledDstSize = dstSize >> buffer.blockSizeLog2;

        final int blockSize = buffer.blockSize;

        final float oneOverBlockSize = (1.0f / blockSize);

        final float filter       = (float) (filterSupport - 1) + oneOverBlockSize;
        final float filterScalar =(float)( 1.0f / Math.pow (filter,3));

        final int sliceSizeLog2 = buffer.sliceSizeLog2;

        final int sliceX = resolveScaledResults(sliceSizeLog2, inputX);
        final int sliceY = resolveScaledResults(sliceSizeLog2, inputY);
        final int sliceZ = resolveScaledResults(sliceSizeLog2, inputZ);

        int baseX = sliceX << sliceSizeLog2;
        int baseY = sliceY << sliceSizeLog2;
        int baseZ = sliceZ << sliceSizeLog2;

        final int inChunkX = MathUtils.getLowerBits(baseX, 4);
        final int inChunkY = MathUtils.getLowerBits(baseY, 4);
        final int inChunkZ = MathUtils.getLowerBits(baseZ, 4);

        int baseIndex = ColorBlendingCache.getArrayIndex(16, inChunkX, inChunkY, inChunkZ);

        Arrays.fill(buffer.sum, 0);

        int newBufferIndexZ = 0;
        int newResultIndexZ = baseIndex;

        for (int z = 0;
             z < srcSize;
             ++z)
        {
            int newIndexX = 0;

            for (int newX = 0;
                 newX < srcSize;
                 ++newX)
            {
                int newSrcIndexY = newIndexX + newBufferIndexZ;
                int newDstIndexY = newIndexX;

                float sumR = 0;
                float sumG = 0;
                float sumB = 0;

                for (int newY = 0;
                     newY < fullFilterDim;
                     ++newY)
                {
                    sumR += buffer.color[newSrcIndexY    ];
                    sumG += buffer.color[newSrcIndexY + 1];
                    sumB += buffer.color[newSrcIndexY + 2];

                    newSrcIndexY += 3 * blendBufferDim;
                }

                newSrcIndexY = newIndexX + newBufferIndexZ;

                int lowerOffset = 0;
                int upperOffset = 3 * fullFilterDim * blendBufferDim;

                int lowerIndex = newSrcIndexY + lowerOffset;
                int upperIndex = newSrcIndexY + upperOffset;

                for (int newY = 0;
                     newY < scaledDstSize;
                     ++newY)
                {
                    float lowerR = buffer.color[lowerIndex    ] * oneOverBlockSize;
                    float lowerG = buffer.color[lowerIndex + 1] * oneOverBlockSize;
                    float lowerB = buffer.color[lowerIndex + 2] * oneOverBlockSize;

                    float upperR = buffer.color[upperIndex    ] * oneOverBlockSize;
                    float upperG = buffer.color[upperIndex + 1] * oneOverBlockSize;
                    float upperB = buffer.color[upperIndex + 2] * oneOverBlockSize;

                    for (int i = 0;
                         i < blockSize;
                         ++i)
                    {
                        sumR += upperR;
                        sumG += upperG;
                        sumB += upperB;

                        buffer.blend[newDstIndexY    ] = sumR;
                        buffer.blend[newDstIndexY + 1] = sumG;
                        buffer.blend[newDstIndexY + 2] = sumB;

                        sumR -= lowerR;
                        sumG -= lowerG;
                        sumB -= lowerB;

                        newDstIndexY += 3 * blendBufferDim;
                    }

                    lowerIndex += 3 * blendBufferDim;
                    upperIndex += 3 * blendBufferDim;
                }

                newIndexX += 3;
            }

            if (z < fullFilterDim)
            {
                int newIndexY = 0;

                for (int newY = 0;
                     newY < dstSize;
                     ++newY)
                {
                    int newSrcIndexX = newIndexY;
                    int newDstIndexX = newIndexY + newBufferIndexZ;
                    int newSumIndexX = newIndexY;

                    float sumR = 0;
                    float sumG = 0;
                    float sumB = 0;

                    for (int newX = 0;
                         newX < fullFilterDim;
                         ++newX)
                    {
                        sumR += buffer.blend[newSrcIndexX    ];
                        sumG += buffer.blend[newSrcIndexX + 1];
                        sumB += buffer.blend[newSrcIndexX + 2];

                        newSrcIndexX += 3;
                    }

                    int lowerOffset = 0;
                    int upperOffset = 3 * fullFilterDim;

                    newSrcIndexX = newIndexY;

                    for (int newX = 0;
                         newX < scaledDstSize;
                         ++newX)
                    {
                        float lowerR = buffer.blend[newSrcIndexX + lowerOffset    ] * oneOverBlockSize;
                        float lowerG = buffer.blend[newSrcIndexX + lowerOffset + 1] * oneOverBlockSize;
                        float lowerB = buffer.blend[newSrcIndexX + lowerOffset + 2] * oneOverBlockSize;

                        float upperR = buffer.blend[newSrcIndexX + upperOffset    ] * oneOverBlockSize;
                        float upperG = buffer.blend[newSrcIndexX + upperOffset + 1] * oneOverBlockSize;
                        float upperB = buffer.blend[newSrcIndexX + upperOffset + 2] * oneOverBlockSize;

                        for (int i = 0;
                             i < blockSize;
                             ++i)
                        {
                            sumR += upperR;
                            sumG += upperG;
                            sumB += upperB;

                            buffer.color[newDstIndexX    ] = sumR;
                            buffer.color[newDstIndexX + 1] = sumG;
                            buffer.color[newDstIndexX + 2] = sumB;

                            buffer.sum[newSumIndexX    ] += sumR;
                            buffer.sum[newSumIndexX + 1] += sumG;
                            buffer.sum[newSumIndexX + 2] += sumB;

                            sumR -= lowerR;
                            sumG -= lowerG;
                            sumB -= lowerB;

                            newDstIndexX += 3;
                            newSumIndexX += 3;
                        }

                        newSrcIndexX += 3;
                    }

                    newIndexY += 3 * blendBufferDim;
                }
            }
            else
            {
                int resultOffsetX = 0;
                int indexX = 0;

                for (int newY = 0;
                     newY < dstSize;
                     ++newY)
                {
                    int srcIndexZ = indexX;
                    int dstIndexZ = indexX + newBufferIndexZ;
                    int sumIndexZ = indexX;

                    float sumR = 0;
                    float sumG = 0;
                    float sumB = 0;

                    for (int newX = 0;
                         newX < fullFilterDim;
                         ++newX)
                    {
                        sumR += buffer.blend[srcIndexZ    ];
                        sumG += buffer.blend[srcIndexZ + 1];
                        sumB += buffer.blend[srcIndexZ + 2];

                        srcIndexZ += 3;
                    }

                    int lowerOffset = 0;
                    int upperOffset = 3 * fullFilterDim;

                    srcIndexZ = indexX;

                    int finalIndexZ = newResultIndexZ + resultOffsetX;

                    for (int newX = 0;
                         newX < scaledDstSize;
                         ++newX)
                    {
                        float lowerR = buffer.blend[srcIndexZ + lowerOffset    ] * oneOverBlockSize;
                        float lowerG = buffer.blend[srcIndexZ + lowerOffset + 1] * oneOverBlockSize;
                        float lowerB = buffer.blend[srcIndexZ + lowerOffset + 2] * oneOverBlockSize;

                        float upperR = buffer.blend[srcIndexZ + upperOffset    ] * oneOverBlockSize;
                        float upperG = buffer.blend[srcIndexZ + upperOffset + 1] * oneOverBlockSize;
                        float upperB = buffer.blend[srcIndexZ + upperOffset + 2] * oneOverBlockSize;

                        int lowerYOffset = 3 * -(filterSupport - 1) * blendBufferDim * blendBufferDim;

                        for (int i = 0;
                             i < blockSize;
                             ++i)
                        {
                            sumR += upperR;
                            sumG += upperG;
                            sumB += upperB;

                            buffer.color[dstIndexZ    ] = sumR;
                            buffer.color[dstIndexZ + 1] = sumG;
                            buffer.color[dstIndexZ + 2] = sumB;

                            float lowerYRV = buffer.color[dstIndexZ + lowerYOffset    ];
                            float lowerYGV = buffer.color[dstIndexZ + lowerYOffset + 1];
                            float lowerYBV = buffer.color[dstIndexZ + lowerYOffset + 2];

                            float lowerYR = lowerYRV * oneOverBlockSize;
                            float lowerYG = lowerYGV * oneOverBlockSize;
                            float lowerYB = lowerYBV * oneOverBlockSize;

                            float upperYR = sumR * oneOverBlockSize;
                            float upperYG = sumG * oneOverBlockSize;
                            float upperYB = sumB * oneOverBlockSize;

                            float valueR = buffer.sum[sumIndexZ    ];
                            float valueG = buffer.sum[sumIndexZ + 1];
                            float valueB = buffer.sum[sumIndexZ + 2];

                            for (int j = 0;
                                 j < blockSize;
                                 ++j)
                            {
                                valueR += upperYR;
                                valueG += upperYG;
                                valueB += upperYB;

                                int finalIndexY = finalIndexZ + 256 * j;

                                float filterR = valueR * filterScalar;
                                float filterG = valueG * filterScalar;
                                float filterB = valueB * filterScalar;

                                blendChunk.data[finalIndexY] =  ColorConverter.convertOKLabsTosRGBAInt(filterR, filterG, filterB);

                                valueR -= lowerYR;
                                valueG -= lowerYG;
                                valueB -= lowerYB;
                            }

                            buffer.sum[sumIndexZ    ] += sumR - lowerYRV;
                            buffer.sum[sumIndexZ + 1] += sumG - lowerYGV;
                            buffer.sum[sumIndexZ + 2] += sumB - lowerYBV;

                            sumR -= lowerR;
                            sumG -= lowerG;
                            sumB -= lowerB;

                            dstIndexZ += 3;
                            sumIndexZ += 3;

                            finalIndexZ += 1;
                        }

                        srcIndexZ += 3;
                    }

                    indexX += 3 * blendBufferDim;

                    resultOffsetX += 16;
                }

                newResultIndexZ += blockSize * 16 * 16;
            }

            newBufferIndexZ += 3 * blendBufferDim * blendBufferDim;
        }
    }

    public static void
    fillBlendChunkRegionWithColor(
            BlendingChunk blendChunk,
            int        color,
            int        baseIndex,
            int        dim)
    {
        int indexZ = baseIndex;

        for (int z = 0;
             z < dim;
             ++z)
        {
            int indexY = indexZ;

            for (int y = 0;
                 y < dim;
                 ++y)
            {
                for (int x = 0;
                     x < dim;
                     ++x)
                {
                    blendChunk.data[indexY + x] = color;
                }

                indexY += 16;
            }

            indexZ += 16 * 16;
        }
    }

    public static void fillBlendChunkSliceWithColor(
            BlendingChunk blendChunk,
            int        color,
            int        sliceSizeLog2,
            int        x,
            int        y,
            int        z)
    {
        final int sliceSize = shiftLogrithmicSliceSize(sliceSizeLog2);

        final int sliceX = resolveScaledResults(sliceSizeLog2, x);
        final int sliceY = resolveScaledResults(sliceSizeLog2, y);
        final int sliceZ = resolveScaledResults(sliceSizeLog2, z);

        int baseX = sliceX << sliceSizeLog2;
        int baseY = sliceY << sliceSizeLog2;
        int baseZ = sliceZ << sliceSizeLog2;

        final int inChunkX = MathUtils.getLowerBits(baseX, 4);
        final int inChunkY = MathUtils.getLowerBits(baseY, 4);
        final int inChunkZ = MathUtils.getLowerBits(baseZ, 4);

        int baseIndex = ColorBlendingCache.getArrayIndex(16, inChunkX, inChunkY, inChunkZ);

        fillBlendChunkRegionWithColor(
                blendChunk,
                color,
                baseIndex,
                sliceSize);
    }

    public static void gatherColorsDirectly(
            World         world,
            ColorResolver colorResolver,
            BlendingChunk    blendChunk,
            int           requestX,
            int           requestY,
            int           requestZ)
    {
        BlockPos.Mutable blockPos = new BlockPos.Mutable();

        final int sliceSizeLog2 = BlendingConfig.getSliceSizeLog2(0);
        final int sliceSize     = BlendingConfig.getSliceSize(0);

        final int sliceX = resolveScaledResults(sliceSizeLog2, requestX);
        final int sliceY = resolveScaledResults(sliceSizeLog2, requestY);
        final int sliceZ = resolveScaledResults(sliceSizeLog2, requestZ);

        boolean neighborsAreLoaded = neighborChunksAreLoaded(world, sliceSizeLog2, sliceX, sliceZ);

        int baseX = sliceX << sliceSizeLog2;
        int baseY = sliceY << sliceSizeLog2;
        int baseZ = sliceZ << sliceSizeLog2;

        final int inChunkX = MathUtils.getLowerBits(baseX, 4);
        final int inChunkY = MathUtils.getLowerBits(baseY, 4);
        final int inChunkZ = MathUtils.getLowerBits(baseZ, 4);

        int baseIndex = ColorBlendingCache.getArrayIndex(16, inChunkX, inChunkY, inChunkZ);

        if (neighborsAreLoaded)
        {
            int indexZ = baseIndex;

            int indexY = indexZ;
            int worldZ = baseZ;
            for (int z =0; z < sliceSize; ++z) {
                for (int y =0; y < sliceSize; ++y) {
                    int indexX = indexY; int worldY = baseY + y;
                    int worldX = baseX; for (int x =0; x < sliceSize; ++x) {
                        blockPos.set(worldX, worldY, worldZ);
                        int color = getColorAtPosition(world, blockPos, worldX, worldZ, colorResolver);
                        blendChunk.data[indexX] = color; worldX++; indexX++;
                    }
                    indexY += sliceSize;
                }
            indexZ += sliceSize * sliceSize;
                worldZ +=1;
            }
        }
        else
        {
            int centerX = (sliceX << sliceSizeLog2) + (1 << (sliceSizeLog2 - 1));
            int centerY = (sliceY << sliceSizeLog2) + (1 << (sliceSizeLog2 - 1));
            int centerZ = (sliceZ << sliceSizeLog2) + (1 << (sliceSizeLog2 - 1));

            blockPos.set(centerX, centerY, centerZ);

            int color = getColorAtPosition(world, blockPos, centerX, centerZ, colorResolver);

            fillBlendChunkRegionWithColor(blendChunk, color, baseIndex, sliceSize);
        }
    }

    public static void generateColors(
            World world,
            ColorResolver colorResolver,
            int           colorType,
            ColorCache    colorCache,
            BlendingChunk blendChunk,
            Coordinates coordinates)
    {
        final int blendRadius = VanadiumBlendingConfiguration.getBlendingRadius();

        if (blendRadius >  BlendingConfig.BIOME_MINIMUM_BLENDING_RADIUS &&
                blendRadius <= BlendingConfig.BIOME_MAXIMUM_BLENDING_RADIUS)
        {
            BlendingBuffer blendBuffer = acquireBlendBuffer(blendRadius);

            gatherColorsToBlendBuffer(
                    world,
                    colorResolver,
                    colorType,
                    colorCache,
                    blendBuffer,
                    coordinates.x(),
                    coordinates.y(),
                    coordinates.z());

            if (blendBuffer.colorBitsInclusive != blendBuffer.colorBitsExclusive)
            {
                blendColorsForSlice(blendBuffer, blendChunk, coordinates.x(), coordinates.y(), coordinates.z());
            }
            else
            {
                fillBlendChunkSliceWithColor(
                        blendChunk,
                        blendBuffer.colorBitsInclusive,
                        blendBuffer.sliceSizeLog2,
                        coordinates.x(), coordinates.y(), coordinates.z());
            }

            releaseBlendBuffer(blendBuffer);
        }
        else
        {
            gatherColorsDirectly(
                    world,
                    colorResolver,
                    blendChunk,
                    coordinates.x(), coordinates.y(), coordinates.z());
        }
    }
}
