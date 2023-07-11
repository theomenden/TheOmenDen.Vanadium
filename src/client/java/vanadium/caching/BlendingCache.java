package vanadium.caching;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import vanadium.blending.BlendingChunk;

import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class BlendingCache {
    public final ReentrantLock lock;
    public final Long2ObjectLinkedOpenHashMap<BlendingChunk> hash;
    public final Stack<BlendingChunk> freeBlendingChunkStack;
    public final Long2ObjectOpenHashMap<BlendingChunk> invalidatedHashHolder;

    public int invalidationCounter = 0;

    public BlendingCache(int count) {
        lock = new ReentrantLock();
        hash = new Long2ObjectLinkedOpenHashMap<>(count);
        freeBlendingChunkStack = new Stack<>();
        invalidatedHashHolder = new Long2ObjectOpenHashMap<>();

        IntStream.range(0, count).forEach(p -> freeBlendingChunkStack.add(new BlendingChunk()));
    }

    public void releaseChunkFromCache(BlendingChunk chunk) {
        int referenceCount = chunk.releaseReference();

        if(referenceCount == 0
         && lock.tryLock()) {
            freeBlendingChunkStack.push(chunk);
            lock.unlock();
        }
    }

    public void invalidateAllReferences() {
        lock.lock();

        ++invalidationCounter;

        for(BlendingChunk chunk : hash.values()) {
            releaseChunkFromCacheNoLock(chunk);

            chunk.previous = null;
            chunk.next = null;
            chunk.markInvalidated();
        }

        hash.clear();
        invalidatedHashHolder.clear();
        lock.unlock();
    }


}
