package vanadium.caching;

import vanadium.models.Coordinates;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseCacheStrategy {
    private long key;
    private int size;
    private int salt;
    private int age;

    private AtomicInteger referenceCount = new AtomicInteger();

    public BaseCacheStrategy() {
        this.size = 0;
        this.salt = 0;

        this.markInvalid();
    }

    public BaseCacheStrategy(int size, int salt) {
        this.size = Math.max(size, 0);
        this.salt = Math.max(salt, 0);

        this.markInvalid();
    }

    public abstract void invalidateRegionCacheData(Coordinates minimumCoordinates, Coordinates maximumCoordinates);
    public abstract void invalidCacheData();

    public long getCacheKey() {
        return key;
    }

    public int getSize() {
        return size;
    }

    public int getSalt() {
        return salt;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public final int getReferenceCount() {
        return referenceCount.get();
    }

    public final void releaseReference() {
        referenceCount.decrementAndGet();
    }

    public final void acquireReference() {
        referenceCount.incrementAndGet();
    }

    public final boolean isInvalid() {
        return ((this.key ^ this.salt)) == ColorBlendingCache.INVALID_CHUNK_KEY;
    }

    public final void markInvalid() {
        this.key = ColorBlendingCache.INVALID_CHUNK_KEY ^ salt;
    }
}
