package vanadium.mixin.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import me.jellysquid.mods.sodium.client.world.biome.BlockColorCache;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.client.color.block.BlockColorProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.VanadiumBlendingClient;
import vanadium.blending.BlendingChunk;
import vanadium.blending.BlendingConfig;
import vanadium.blending.ColorBlending;
import vanadium.caching.BlendingCache;
import vanadium.caching.ColorBlendingCache;
import vanadium.caching.ColorCache;
import vanadium.caching.LocalCache;
import vanadium.enums.BiomeColorTypes;
import vanadium.models.Coordinates;
import vanadium.resolvers.VanadiumColorResolverCustomCompatibility;
import vanadium.util.ColorCacheUtils;

import java.util.function.Supplier;

@Mixin(value = ClientWorld.class)
public abstract class ClientWorldMixin extends World {
    @Unique
    private final BlendingCache vanadium$blendedColorCache = new BlendingCache(1024);

    @Unique
    public final ColorCache vanadium$chunkColorCache = new ColorCache(1024);

    @Unique
    private final ThreadLocal<LocalCache> vanadium$threadLocalCache = ThreadLocal.withInitial(LocalCache::new);

    protected ClientWorldMixin(MutableWorldProperties properties,
                               RegistryKey<World> registryRef,
                               DynamicRegistryManager registryManager,
                               RegistryEntry<DimensionType> dimensionEntry,
                               Supplier<Profiler> profiler,
                               boolean isClient,
                               boolean debugWorld,
                               long biomeAccess,
                               int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(method="reloadColor", at = @At("HEAD"))
    public void onReloadColorCaches(CallbackInfo ci) {
        vanadium$blendedColorCache.invalidateAllChunks();

        int blendingRadius = VanadiumBlendingClient.getBlendingRadius();
        vanadium$chunkColorCache.invalidateAllCachesInRadius(blendingRadius);
    }

    @Inject(method="resetChunkColor", at= @At("HEAD"))
    public void onResetChunkColor(ChunkPos chunkPos, CallbackInfo ci) {
        vanadium$blendedColorCache.invalidateChunk(chunkPos.x, chunkPos.z);
    }


    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getColor(BlockPos blockPosIn, ColorResolver colorResolverIn) {
        final Coordinates baseCoordinates = new Coordinates(blockPosIn.getX(), blockPosIn.getY(), blockPosIn.getZ());
        final Coordinates chunkCoordinates = new Coordinates(baseCoordinates.x() >> 4, baseCoordinates.y() >> 4, baseCoordinates.z() >> 4);
        final Coordinates blockCoordinates = new Coordinates(baseCoordinates.x() & 15, baseCoordinates.y() & 15, baseCoordinates.z() & 15);

        LocalCache localCache = vanadium$threadLocalCache.get();

        BlendingChunk chunk = null;
        int colorType;

        if(localCache.latestColorResolver == colorResolverIn) {
            colorType = localCache.lastColorType;
            long key = ColorBlendingCache.getChunkCacheKey(chunkCoordinates, colorType);

            if(localCache.lastBlendedChunk.key == key) {
                chunk = localCache.lastBlendedChunk;
            }
        } else {
            colorType = calculateColorType(colorResolverIn, localCache);

            long key = ColorBlendingCache.getChunkCacheKey(chunkCoordinates, colorType);

            BlendingChunk cachedChunk = localCache.blendedChunks[colorType];

            if(cachedChunk.key == key) {
                chunk = cachedChunk;
            }
        }

        if(chunk == null) {
            chunk = vanadium$blendedColorCache.getOrInitializeChunk(chunkCoordinates, colorType);

            localCache.putChunkInBlendedCache(vanadium$blendedColorCache, chunk, colorType, colorResolverIn);
        }

        int index = ColorBlendingCache.getArrayIndex(16, blockCoordinates);

        int color = chunk.data[index];

        if(color == 0) {
            ColorBlending.generateColors(this, colorResolverIn, colorType, vanadium$chunkColorCache, chunk, baseCoordinates);
        }

        return color;
    }


    private static int calculateColorType(ColorResolver colorResolver, LocalCache localCache) {
        if(colorResolver == BiomeColors.GRASS_COLOR) {
            return BiomeColorTypes.INSTANCE.grass();
        }

        if(colorResolver == BiomeColors.WATER_COLOR) {
            return BiomeColorTypes.INSTANCE.water();
        }

        if(colorResolver == BiomeColors.FOLIAGE_COLOR) {
            return BiomeColorTypes.INSTANCE.foliage();
        }

        int resolvedColorType = VanadiumColorResolverCustomCompatibility.getcolorType(colorResolver);

        if(resolvedColorType >= localCache.blendedChunksCount) {
            localCache.reallocateBlendedChunkyArray(resolvedColorType);
        }

        return resolvedColorType;
    }
}
