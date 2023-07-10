package vanadium.blending;

import java.util.concurrent.atomic.AtomicInteger;

public final class ChunkBlender {
    public int[] data;
    public long key;
    public int invalidationCounter;

    public AtomicInteger referencedCount = new AtomicInteger();

    public long invalidationKey;
    ChunkBlender previous;
    ChunkBlender next;

    public ChunkBlender() {
        this.data = new int[4096];
        this.markAsInvalid();
    }

    public void markAsInvalid() {
        key = -1;
    }
}
