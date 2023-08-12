package vanadium.biomeblending.blending;

import java.util.concurrent.atomic.AtomicInteger;

public class BlendingChunk {
    public int[] data;
    public long key;
    public int invalidationCounter;
    public AtomicInteger referenceCounter = new AtomicInteger();
    public long invalidationKey;
    public BlendingChunk next;
    public BlendingChunk previous;

    public BlendingChunk() {
        this.data = new int[4096];
        this.markAsInvalidated();
    }

    public int getReferencesCount() {
        return referenceCounter.get();
    }

    public int releaseReference() {
        return referenceCounter.decrementAndGet();
    }

    public void acquireReference() {
        referenceCounter.incrementAndGet();
    }

    public void markAsInvalidated() {
        key = ColorBlendingCache.INVALID_CHUNK_KEY;
    }

    public void removeFromLinkedList() {
        if(this.previous!= null) {
            this.previous.next = this.next;
        }

        if(this.next!= null) {
            this.next.previous = this.previous;
        }

        this.next = null;
        this.previous = null;
    }
}
