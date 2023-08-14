package vanadium.mixin.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.world.BiomeColorCache;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;
import vanadium.biomeblending.blending.BlendingChunk;
import vanadium.biomeblending.caching.BlendingCache;
import vanadium.biomeblending.caching.ColorBlendingCache;
import vanadium.biomeblending.caching.ColorCache;
import vanadium.biomeblending.caching.LocalCache;
import vanadium.customcolors.VanadiumColorResolverCompatibility;
import vanadium.customcolors.blending.ColorBlending;
import vanadium.models.NonBlockingThreadLocal;
import vanadium.models.records.BiomeColorTypes;
import vanadium.models.records.Coordinates;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World {
    @Unique
    public final ColorCache vanadium$chunkColorCache = new ColorCache(1024);
    @Shadow
    private final Object2ObjectArrayMap<ColorResolver, BiomeColorCache> colorCache = new Object2ObjectArrayMap<>();

    @Shadow public abstract int calculateColor(BlockPos pos, ColorResolver colorResolver);

    @Unique
    private final ThreadLocal<LocalCache> vanadiumBiomeBlending$threadLocalCache = NonBlockingThreadLocal.withInitial(LocalCache::new);

    @Unique
    private final BlendingCache vanadium$blendingColorCache = new BlendingCache(1024);

    protected ClientWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(method = "getColor",
            at=@At(
                    value = "INVOKE",
            target = "Lnet/minecraft/client/world/BiomeColorCache;getBiomeColor(Lnet/minecraft/util/math/BlockPos;)I"
    ))
    private void calculateColorType(BlockPos pos, ColorResolver colorResolver, CallbackInfoReturnable<Integer> cir) {
        if(this.colorCache.get(colorResolver) == null) {
           this.colorCache.put(colorResolver, new BiomeColorCache(pos1 -> this.calculateColor(pos, colorResolver)));
        }

        final Coordinates positionedCoordinates = new Coordinates(pos.getX(), pos.getY(), pos.getZ());
        final Coordinates chunkCoordinates = new Coordinates(positionedCoordinates.x() >> 4, positionedCoordinates.y() >> 4, positionedCoordinates.z() >> 4);
        final Coordinates blockCoordinates = new Coordinates(positionedCoordinates.x() & 15, positionedCoordinates.y() & 15, positionedCoordinates.z() & 15);

        LocalCache localCache = vanadiumBiomeBlending$threadLocalCache.get();

        BlendingChunk chunk = null;
        int colorType;

        if (localCache.latestColorResolver == colorResolver) {
            colorType = localCache.lastColorType;
            long cachedChunkKey = ColorBlendingCache.getChunkCacheKey(chunkCoordinates, colorType);

            if (localCache.lastBlendedChunk.key == cachedChunkKey) {
                chunk = localCache.lastBlendedChunk;
            }
        } else {
            colorType = calculateColorType(colorResolver, localCache);

            long cachedChunkKey = ColorBlendingCache.getChunkCacheKey(chunkCoordinates, colorType);
            BlendingChunk cachedChunk = localCache.blendedChunks[colorType];
            if (localCache.lastBlendedChunk.key == cachedChunkKey) {
                chunk = cachedChunk;
            }
        }

        if(chunk == null) {
            chunk = vanadium$blendingColorCache.getOrInitializeChunk(chunkCoordinates, colorType);
            localCache.putChunkInBlendedCache(vanadium$blendingColorCache, chunk, colorType, colorResolver);
        }

        int index = ColorBlendingCache.getArrayIndex(16, blockCoordinates);
        int color = chunk.data[index];

        if(color == 0) {
            ColorBlending.generateColors(
                    this,
                    colorResolver,
                    colorType,
                    vanadium$chunkColorCache,
                    chunk,
                    positionedCoordinates
            );

            color = chunk.data[index];
        }

        cir.setReturnValue(color);
    }


    @Inject(method="reloadColor", at = @At("HEAD"))
    public void onClearColorCache(CallbackInfo ci) {
        vanadium$blendingColorCache.invalidateAllChunks();
        int blendingRadius = Vanadium.configuration.blendingRadius;
        vanadium$chunkColorCache.invalidateAllCachesInRadius(blendingRadius);
    }

    @Inject(method="resetChunkColor", at = @At("HEAD"))
    public void onResetChunkColor(ChunkPos chunkPos, CallbackInfo ci) {
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;

        vanadium$blendingColorCache.invalidateChunk(chunkX, chunkZ);
    }

    private static int calculateColorType(ColorResolver colorResolver, LocalCache localCache) {
        if (colorResolver == BiomeColors.GRASS_COLOR) {
            return BiomeColorTypes.INSTANCE.grass();
        }

        if (colorResolver == BiomeColors.WATER_COLOR) {
            return BiomeColorTypes.INSTANCE.water();
        }

        if (colorResolver == BiomeColors.FOLIAGE_COLOR) {
            return BiomeColorTypes.INSTANCE.foliage();
        }

        int resolvedColorType = VanadiumColorResolverCompatibility.getColorType(colorResolver);

        if (resolvedColorType >= localCache.blendedChunksCount) {
            localCache.reallocateBlendedChunkyArray(resolvedColorType);
        }

        return resolvedColorType;
    }


}
