package vanadium.blending;

public final class BlendingConfig {

    public static final int BIOME_MAXIMUM_BLENDING_RADIUS = 14;
    public static final int BIOME_MINIMUM_BLENDING_RADIUS = 0;

    public static final byte[] blendedRadiusConfiguration = {
            2,0,
            3,0,
            3,1,
            3,1,
            3,1,
            3,1,
            4,2,
            4,2,
            4,2,
            4,2,
            4,2,
            4,2,
            4,2,
            4,2,
            4,2
    };

    public static int getSliceSizeLog2(int blendingRadius) {
        return blendedRadiusConfiguration[(blendingRadius << 1)];
    }

    public static int getBlockSizeLog2(int blendingRadius) {
        return blendedRadiusConfiguration[(blendingRadius << 1) + 1];
    }

    public static int getSliceSize(int blendingRadius) {
        return 1 << getSliceSizeLog2(blendingRadius);
    }

    public static int getBlendingSize(int blendingRadius) {
        final int blockSizeLog2 = getBlockSizeLog2(blendingRadius);
        final int sliceSizeLog2 = getSliceSizeLog2(blendingRadius);
        final int sliceSize = 1 << sliceSizeLog2;
        final int blendingSize = sliceSize + (2 * blendingRadius);

        return blendingSize >> blockSizeLog2;
    }

    public static int getBlendingBufferSize(int blendingRadius) {
        int blendingSize = getBlendingSize(blendingRadius);
        int sliceSize = getSliceSize(blendingRadius);

        return Math.max(blendingSize, sliceSize);
    }

    public static int getFilterSupport(int blendingRadius) {
        final int sliceSizeLog2 = getSliceSizeLog2(blendingRadius);
        final int blockSizeLog2 = getBlockSizeLog2(blendingRadius);

        final int sliceSize = 1 << sliceSizeLog2;
        final int blendingDimension = BlendingConfig.getBlendingSize(blendingRadius);

        final int scaledSliceSize = sliceSize >> blockSizeLog2;

        return blendingDimension - scaledSliceSize + 1;
    }
}
