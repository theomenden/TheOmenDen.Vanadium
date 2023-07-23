package vanadium.caching;

import com.google.common.util.concurrent.Striped;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import vanadium.blending.BlendingConfig;
import vanadium.enums.BiomeColorTypes;
import vanadium.models.Coordinates;

import java.util.concurrent.locks.Lock;
import java.util.stream.IntStream;

public abstract class SlicingCachingStrategy<T extends BaseSlice> {
    public final static int AVAILABLE_BUCKETS = 16;
    public final Long2ObjectLinkedOpenHashMap<T>[] hashMapStorageContainer;
    public final Lock[] locks;

    public final int totalSlices;
    public int sliceSize;
    public SlicingCachingStrategy(int count) {
        this.totalSlices = count;
        int countPerBucket = count / AVAILABLE_BUCKETS;
        hashMapStorageContainer = new Long2ObjectLinkedOpenHashMap[AVAILABLE_BUCKETS];

        IntStream
                .range(0, AVAILABLE_BUCKETS)
                .forEach(i -> hashMapStorageContainer[i] = new Long2ObjectLinkedOpenHashMap<>(countPerBucket));

        locks = new Lock[AVAILABLE_BUCKETS];
        var stripedLocks = Striped.lock(AVAILABLE_BUCKETS);

        IntStream
                .range(0, AVAILABLE_BUCKETS)
                .forEach(i -> locks[i] = stripedLocks.get(i));
    }

    public abstract T createSlice(int sliceSize, int salt);

    public final void redistributeSlices(int sliceSize) {
        this.sliceSize = sliceSize;

        int countPerBucket = this.totalSlices / AVAILABLE_BUCKETS;

        for(int i = 0; i < AVAILABLE_BUCKETS; ++i) {
            Lock stripedLock = locks[i];

            if(stripedLock.tryLock()) {
                try {
                    Long2ObjectLinkedOpenHashMap<T> hash = hashMapStorageContainer[i];

                    hash.clear();

                    for(int index = 0; index < countPerBucket; ++index) {
                        T slice = createSlice(sliceSize, index);

                        hash.put(slice.getCacheKey(), slice);
                    }
                } finally {
                    stripedLock.unlock();
                }
                continue;
            }

            throw new RuntimeException("Could not acquire lock for bucket " + i);
        }
    }

    public final void invalidateAllCachesInRadius(int blendedRadius){
        this.sliceSize = BlendingConfig.getSliceSize(blendedRadius);

        redistributeSlices(sliceSize);
    }

    public final void releaseSliceFromCache(T slice) {
        slice.releaseReference();
    }

    public final T getOrInitSliceByCoordinates(int slizeSize, int x, int y, int z, int colorType, boolean shouldTryLocking)
     {
        Coordinates sliceCoordinates = new Coordinates(x, y, z);

        return getOrInitSlice(sliceSize, sliceCoordinates, colorType, shouldTryLocking);
    }
    public final T getOrInitSlice(int sliceSize, Coordinates sliceCoordinates, int colorType, boolean shouldTryLocking){
        long key = ColorBlendingCache.getChunkCacheKey(sliceCoordinates, colorType);

        int bucketIndex = getBucketIndex(sliceCoordinates);

        Lock stripedLock = locks[bucketIndex];
        Long2ObjectLinkedOpenHashMap<T> hash = hashMapStorageContainer[bucketIndex];

        T slice = null;

        if (shouldTryLocking && stripedLock.tryLock()) {
            try {
                slice = hash.getAndMoveToFirst(key);
                if (slice == null || slice.getSize() != sliceSize) {
                    slice = createSlice(sliceSize,0);
                    hash.putAndMoveToFirst(key, slice);
                } else {
                    slice.acquireReference();
                    hash.remove(key);
                    hash.putAndMoveToFirst(key, slice);
                }

                slice.setCacheKey(key);
                slice.invalidateCacheData();
            } finally {
                stripedLock.unlock();
            }
        }
        return slice;
    }

    private int getBucketIndex(Coordinates coordinates) {
        return (coordinates.x() ^ coordinates.y() ^ coordinates.z()) & (AVAILABLE_BUCKETS - 1);
    }

    private int getStripe(long key) {
        return (int) (key % locks.length);
    }


}
