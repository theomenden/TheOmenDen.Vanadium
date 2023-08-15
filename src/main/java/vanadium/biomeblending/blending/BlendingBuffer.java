package vanadium.biomeblending.blending;

public class BlendingBuffer {
    public final float[] color;
    public final float[] blend;
    public final float[] sum;

    public final int blendingRadius;
    public final int sliceSizeLog2;
    public final int blockSizeLog2;
    public final int sliceSize;
    public final int blockSize;
    public final int blendingSize;
    public final int blendingBufferSize;
    public final int scaledBlendingDiameter;
    public int colorBitsExclusive;
    public int colorBitsInclusive;

    public BlendingBuffer(int blendingRadius) {
        this.blendingRadius = blendingRadius;
        this.sliceSizeLog2 = BlendingConfig.getSliceSizeLog2(blendingRadius);
        this.blockSizeLog2 = BlendingConfig.getBlockSizeLog2(blendingRadius);

        this.sliceSize = 1 << sliceSizeLog2;
        this.blockSize = 1 << blockSizeLog2;

        this.blendingSize = BlendingConfig.getBlendingSize(blendingRadius);
        this.blendingBufferSize = BlendingConfig.getBlendingBufferSize(blendingRadius);

        this.scaledBlendingDiameter = (2 * blendingRadius) >> blockSizeLog2;

        this.color = new float[3 * (int)(Math.pow(blendingBufferSize, 3))];
        this.blend = new float[3 * (int)(Math.pow(blendingBufferSize, 2))];
        this.sum = new float[3 * (int)(Math.pow(blendingBufferSize, 2))];

        colorBitsExclusive = 0xFFFFFFFF;
        colorBitsInclusive = 0;
    }
}
