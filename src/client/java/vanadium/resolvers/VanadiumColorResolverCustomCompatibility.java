package vanadium.resolvers;

import net.minecraft.world.biome.ColorResolver;
import vanadium.enums.BiomeColorTypes;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public final class VanadiumColorResolverCustomCompatibility {
    public static final ReentrantLock lock = new ReentrantLock();
    public static final ConcurrentHashMap<ColorResolver, Integer> currentKnownColorResolvers = new ConcurrentHashMap<>();

    public static int nextColorResolverId = BiomeColorTypes.FOLIAGE.ordinal() + 1;

    public static int nextColorResolverId() {
        int result = nextColorResolverId++;
        return result;
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

    public static int getcolorType(ColorResolver resolver) {
        Integer result = currentKnownColorResolvers.get(resolver);

        if(result == null) {
            result = addColorResolver(resolver);
        }

        return result;

    }
}
