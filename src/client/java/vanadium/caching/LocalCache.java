package vanadium.caching;

import net.minecraft.world.biome.ColorResolver;
import vanadium.blending.BlendingChunk;
import vanadium.enums.BiomeColorTypes;

import java.util.stream.IntStream;

public final class LocalCache {
    public ColorResolver latestColorResolver;
    public BlendingChunk lastBlendedChunk;
    public int lastColorType;

    public BlendingChunk[] blendedChunks = new BlendingChunk[3];
    public int blendedChunksCount = 3;

    public LocalCache() {
        IntStream.range(0, blendedChunksCount).forEach(i -> blendedChunks[i] = createBlendingChunk());
        lastBlendedChunk = blendedChunks[0];
    }

    public static BlendingChunk createBlendingChunk() {
        BlendingChunk resolved = new BlendingChunk();

        resolved.acquireReference();

        return resolved;
    }

    public void putChunkInBlendedCache(BlendingCache cache, BlendingChunk chunk, int colorType, ColorResolver resolver) {
        BlendingChunk previousChunk = this.blendedChunks[colorType];

        this.latestColorResolver = resolver;
        this.lastBlendedChunk = chunk;
        this.lastColorType = colorType;

        cache.releaseChunk(previousChunk);

        this.blendedChunks[colorType] = chunk;
    }

    public void reallocateBlendedChunkyArray(int colorType) {
        int oldBlendedChunksCount = this.blendedChunksCount;
        int newBlendedChunksCount = colorType + 1;

        if(newBlendedChunksCount > oldBlendedChunksCount) {
            BlendingChunk[] oldBlendedChunks = this.blendedChunks;
            BlendingChunk[] newBlendedChunks = new BlendingChunk[newBlendedChunksCount];

            IntStream.range(0, oldBlendedChunksCount).forEach(i -> newBlendedChunks[i] = oldBlendedChunks[i]);
            IntStream.range(oldBlendedChunksCount, newBlendedChunksCount).forEach(i -> newBlendedChunks[i] = createBlendingChunk());

            this.blendedChunks = newBlendedChunks;
            this.blendedChunksCount = newBlendedChunksCount;
        }
    }
}
