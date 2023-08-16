package vanadium.biomeblending.caching.strategies;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import vanadium.biomeblending.blending.BlendingConfig;
import vanadium.biomeblending.caching.ColorBlendingCache;
import vanadium.models.records.Coordinates;

import java.util.concurrent.locks.StampedLock;
import java.util.stream.IntStream;

public abstract class SlicedCacheStrategy<T extends BaseSlice> {
    public final static int AVAILABLE_BUCKETS = 8;
    public final Long2ObjectLinkedOpenHashMap<T>[] hashMapStorageContainer;
    public final StampedLock[] locks;

    public final int totalSlices;
    public int sliceSize;
    public SlicedCacheStrategy(int count) {
        this.totalSlices = count;
        int countPerBucket = count / AVAILABLE_BUCKETS;
        hashMapStorageContainer = new Long2ObjectLinkedOpenHashMap[AVAILABLE_BUCKETS];

        IntStream
                .range(0, AVAILABLE_BUCKETS)
                .forEach(i -> hashMapStorageContainer[i] = new Long2ObjectLinkedOpenHashMap<>(countPerBucket));

        locks = new StampedLock[AVAILABLE_BUCKETS];
        IntStream
                .range(0, AVAILABLE_BUCKETS)
                .forEach(i -> locks[i] = new StampedLock());
    }

    public abstract T createSlice(int sliceSize, int salt);

    public final void redistributeSlices(int sliceSize) {
        this.sliceSize = sliceSize;

        int countPerBucket = this.totalSlices / AVAILABLE_BUCKETS;

        IntStream
                .range(0, AVAILABLE_BUCKETS)
                .forEach(i -> {
                    StampedLock stampedLock = locks[i];
                    Long2ObjectLinkedOpenHashMap<T> hash = hashMapStorageContainer[i];
                    long stamp = stampedLock.writeLock();
                    hash.clear();
                    IntStream
                            .range(0, countPerBucket)
                            .forEach(index -> {
                                T slice = createSlice(sliceSize, index);
                                hash.put(slice.getCacheKey(), slice);
                            });
                    stampedLock.unlockWrite(stamp);
                });
    }

    public final void invalidateAllCachesInRadius(int blendedRadius){
        this.sliceSize = BlendingConfig.getSliceSize(blendedRadius);

        redistributeSlices(sliceSize);
    }

    public final void releaseSliceFromCache(T slice) {
        slice.releaseReference();
    }

    public final T getOrInitSliceByCoordinates(int sliceSize, int x, int y, int z, int colorType, boolean shouldTryLocking)
    {
        Coordinates sliceCoordinates = new Coordinates(x, y, z);

        return getOrInitSlice(sliceSize, sliceCoordinates, colorType, shouldTryLocking);
    }
    public final T getOrInitSlice(int sliceSize, Coordinates sliceCoordinates, int colorType, boolean shouldTryLocking){
        long key = ColorBlendingCache.getChunkCacheKey(sliceCoordinates, colorType);

        int bucketIndex = getBucketIndex(sliceCoordinates);

        StampedLock stampedLock = locks[bucketIndex];
        Long2ObjectLinkedOpenHashMap<T> hash = hashMapStorageContainer[bucketIndex];

        T slice = null;

        long stamp;

        if(shouldTryLocking) {
            stamp = stampedLock.tryWriteLock();
        } else {
            stamp = stampedLock.writeLock();
        }

        if(stamp != 0) {
            slice = hash.getAndMoveToFirst(key);

            if (slice == null) {
                while (true) {
                    slice = hash.removeLast();

                    if (slice.getReferenceCount() == 0) {
                        break;
                    } else {
                        hash.putAndMoveToFirst(slice.getCacheKey(), slice);
                    }
                }
                slice.setCacheKey(key);
                slice.invalidateCacheData();

                hash.putAndMoveToFirst(slice.getCacheKey(), slice);

            }

            if (slice.getSize() == sliceSize) {
                slice.acquireReference();
            } else {
                slice = createSlice(sliceSize, 0);
            }

            stampedLock.unlockWrite(stamp);
        }
        return slice;
    }

    private int getBucketIndex(Coordinates coordinates) {
        return (coordinates.x() ^ coordinates.y() ^ coordinates.z()) & (AVAILABLE_BUCKETS - 1);
    }

}
