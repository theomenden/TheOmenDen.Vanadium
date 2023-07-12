package vanadium.blending;

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
}
