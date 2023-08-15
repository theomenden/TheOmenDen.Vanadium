package vanadium.biomeblending.compatibility;

import net.minecraft.world.biome.ColorResolver;
import vanadium.models.records.BiomeColorTypes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public final class CustomColorCompatibility {
    public static final ReentrantLock lock = new ReentrantLock();
    public static final ConcurrentHashMap<ColorResolver, Integer> knownColorResolvers = new ConcurrentHashMap<>();

    public static int nextColorResolverID = BiomeColorTypes.LAST + 1;

    public static int getNextColorResolverID() {
        return nextColorResolverID++;
    }

    public static int addColorResolver(ColorResolver resolver) {
        lock.lock();
        int result;

        if(!knownColorResolvers.containsKey(resolver)) {
            result = getNextColorResolverID();
            knownColorResolvers.put(resolver, result);
        } else {
            result = knownColorResolvers.get(resolver);
        }

        lock.unlock();
        return result;
    }


    public static int getColorType(ColorResolver resolver) {
        Integer result = knownColorResolvers.get(resolver);

        if(result == null) {
            result = addColorResolver(resolver);
        }

        return  result;
    }
}
