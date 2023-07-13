package vanadium.caching;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import vanadium.blending.BlendingChunk;
import vanadium.enums.BiomeColorTypes;
import vanadium.models.Coordinates;

import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class BlendingCache {

    public final ReentrantLock lock;
    public final Long2ObjectLinkedOpenHashMap<BlendingChunk> hashedChunks;
    public final Stack<BlendingChunk> availableChunks;
    public final Long2ObjectOpenHashMap<BlendingChunk> invalidatedHashedChunks;

    public int invalidationCounter = 0;

    public BlendingCache(int count) {
        lock = new ReentrantLock();
        hashedChunks = new Long2ObjectLinkedOpenHashMap<>(count);
        availableChunks = new Stack<>();
        invalidatedHashedChunks = new Long2ObjectOpenHashMap<>(count);

        IntStream.range(0, count)
                .mapToObj(i -> new BlendingChunk())
                .forEach(availableChunks::add);
    }

    public void invalidateChunk(int chunkXCoord, int chunkZCoord) {
        lock.lock();

        ++invalidationCounter;

        for(int z = -1; z <=1; ++z){
            for(int x = -1; x <=1; ++x){
               var key = ColorBlendingCache.getChunkCacheKey(new Coordinates(chunkXCoord + x, 0, chunkZCoord + z), BiomeColorTypes.GRASS);

               BlendingChunk firstChunkToBlendAgainst = invalidatedHashedChunks.get(key);

                BlendingChunk current = firstChunkToBlendAgainst;
                while (current!= null) {
                BlendingChunk nextChunkToBlendAgainst = current.next;
                hashedChunks.remove(current.invalidationKey);
                removeChunkFromInvalidation(current);
                releaseChunkWithoutLocking(current);

                current.markAsInvalidated();

                current = nextChunkToBlendAgainst;
            }
            }
        }

        lock.unlock();
    }

    public void invalidateAllChunks() {
        lock.lock();

        ++invalidationCounter;

        hashedChunks.values().forEach(chunk -> {
            releaseChunkWithoutLocking(chunk);
            chunk.previous = null;
            chunk.next = null;
            chunk.markAsInvalidated();
        });

        hashedChunks.clear();
        invalidatedHashedChunks.clear();
        lock.unlock();
    }

    public void releaseChunkWithoutLocking(BlendingChunk chunk) {
        int referenceCount = chunk.releaseReference();

        if(referenceCount == 0) {
            availableChunks.push(chunk);
        }
    }

    public void releaseChunk(BlendingChunk chunk) {
        int referenceCount = chunk.releaseReference();

        if(referenceCount == 0) {
            lock.lock();
            availableChunks.push(chunk);
            lock.unlock();
        }
    }

    public void addChunkToInvalidation(BlendingChunk chunk) {
        BlendingChunk otherChunkToBlendAgainst = invalidatedHashedChunks.get(chunk.invalidationKey);

        if(otherChunkToBlendAgainst!= null) {
            chunk.next = otherChunkToBlendAgainst.next;
            chunk.previous = otherChunkToBlendAgainst;

            if(otherChunkToBlendAgainst.next != null){
                otherChunkToBlendAgainst.next.previous = chunk;
            }

            otherChunkToBlendAgainst.next = chunk;

            return;
        }

        invalidatedHashedChunks.put(chunk.invalidationKey, chunk);
    }

    public void removeChunkFromInvalidation(BlendingChunk chunk) {

        if(chunk.previous == null) {
            invalidatedHashedChunks.remove(chunk.invalidationKey);

            if(chunk.next != null) {
                invalidatedHashedChunks.put(chunk.invalidationKey, chunk.next);
            }
        }

        invalidatedHashedChunks.remove(chunk.invalidationKey);
    }

    public BlendingChunk getOrInitializeChunk(int chunkXCoord, int chunkYCoord, int chunkZCoord, BiomeColorTypes biomeColorType) {
        var key = ColorBlendingCache.getChunkCacheKey(new Coordinates(chunkXCoord, chunkYCoord, chunkZCoord), biomeColorType);

        lock.lock();

        BlendingChunk chunk = hashedChunks.getAndMoveToFirst(key);

        if(chunk == null){
        if(!availableChunks.empty()) {
            chunk = availableChunks.pop();
        } else {
            for(;;) {
                chunk = hashedChunks.removeLast();

                if(chunk.getReferencesCount() == 1) {
                    chunk.releaseReference();
                    break;
                }

                hashedChunks.put(chunk.key, chunk);
            }
            removeChunkFromInvalidation(chunk);
        }

        var invalidationKey = ColorBlendingCache.getChunkCacheKey(new Coordinates(chunkXCoord, 0, chunkZCoord), BiomeColorTypes.GRASS);

        chunk.key = key;
        chunk.invalidationCounter = invalidationCounter;
        chunk.invalidationKey = invalidationKey;
        chunk.previous = null;
        chunk.next = null;

        Arrays.fill(chunk.data, 0);

        hashedChunks.putAndMoveToFirst(chunk.key, chunk);
        addChunkToInvalidation(chunk);
        chunk.acquireReference();
    }
        chunk.acquireReference();
        lock.unlock();
        return chunk;
    }
}