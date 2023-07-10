package vanadium.blending;

public final class ColorBlendBuffer {

    public final float[] colorStore;
    public final float[] blendStore;
    public final float[] sumStore;

    public final int BlendRadius;
    public final int scaledBlendDiameter;

    public final int BlendSize;
    public final int BlendBufferSize;
    public final int BlockSize;

    public int InclusiveColorBits;
    public int ExclusiveColorBits;

    public ColorBlendBuffer(int blendRadius) {
        this.BlendRadius = blendRadius;

        InclusiveColorBits = 0xFFFFFFFF;
        ExclusiveColorBits = 0;
    }
}
