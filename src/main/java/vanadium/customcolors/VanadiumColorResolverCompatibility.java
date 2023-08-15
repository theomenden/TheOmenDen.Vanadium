package vanadium.customcolors;

import net.minecraft.world.biome.ColorResolver;
import vanadium.models.records.BiomeColorTypes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public final class VanadiumColorResolverCompatibility {
    public static final ReentrantLock lock = new ReentrantLock();
    public static final ConcurrentHashMap<ColorResolver, Integer> currentKnownColorResolvers = new ConcurrentHashMap<>();

    public static int nextColorResolverId = BiomeColorTypes.INSTANCE.foliage() + 1;

    public static int nextColorResolverId() {
        return nextColorResolverId++;
    }

    public static int addColorResolver(ColorResolver resolver) {
        lock.lock();
        int result;

        if(!currentKnownColorResolvers.containsKey(resolver)) {
            result = nextColorResolverId();
            currentKnownColorResolvers.put(resolver, result);
        } else {
            result = currentKnownColorResolvers.get(resolver);
        }

        lock.unlock();
        return result;
    }

    public static int getColorType(ColorResolver resolver) {
        Integer result = currentKnownColorResolvers.get(resolver);

        if(result == null) {
            result = addColorResolver(resolver);
        }

        return result;

    }
}
