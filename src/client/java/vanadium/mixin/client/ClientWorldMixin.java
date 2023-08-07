package vanadium.mixin.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.entry.Vanadium;
import vanadium.blending.BlendingChunk;
import vanadium.blending.ColorBlending;
import vanadium.caching.BlendingCache;
import vanadium.caching.ColorBlendingCache;
import vanadium.caching.ColorCache;
import vanadium.caching.LocalCache;
import vanadium.colormapping.BiomeColorMappings;
import vanadium.configuration.VanadiumBlendingConfiguration;
import vanadium.enums.BiomeColorTypes;
import vanadium.models.Coordinates;
import vanadium.models.NonBlockingThreadLocal;
import vanadium.resolvers.ExtendedColorResolver;
import vanadium.resolvers.VanadiumColorResolverCustomCompatibility;

import java.util.function.Supplier;

@Mixin(value = ClientLevel.class)
public abstract class ClientWorldMixin extends Level {

    @Unique
    public final ColorCache vanadium$chunkColorCache = new ColorCache(1024);
    @Shadow
    private final Object2ObjectArrayMap<ColorResolver, BlockTintCache> tintCaches = new Object2ObjectArrayMap<>();

    @Shadow public abstract int calculateBlockTint(BlockPos blockPos, ColorResolver colorResolver);

    @Unique
    private final BlendingCache vanadium$blendedColorCache = new BlendingCache(1024);
    @Unique
    private final ThreadLocal<LocalCache> vanadium$threadLocalCache = NonBlockingThreadLocal.withInitial(LocalCache::new);

    protected ClientWorldMixin(
            WritableLevelData writableLevelData,
            ResourceKey<Level> resourceKey,
            RegistryAccess registryAccess,
            Holder<DimensionType> dimensionTypeHolder,
            Supplier<ProfilerFiller> profilerFillerSupplier,
            boolean isClientSided,
            boolean isInDebug,
            long biomeZoomSeedLevel,
            int maximumUpdatesForChainedNeighbors

    ) {
        super(writableLevelData, resourceKey, registryAccess, dimensionTypeHolder, profilerFillerSupplier, isClientSided, isInDebug, biomeZoomSeedLevel, maximumUpdatesForChainedNeighbors);
    }

    private static int calculateColorType(ColorResolver colorResolver, LocalCache localCache) {
        if (colorResolver == BiomeColors.GRASS_COLOR_RESOLVER) {
            return BiomeColorTypes.INSTANCE.grass();
        }

        if (colorResolver == BiomeColors.WATER_COLOR_RESOLVER) {
            return BiomeColorTypes.INSTANCE.water();
        }

        if (colorResolver == BiomeColors.FOLIAGE_COLOR_RESOLVER) {
            return BiomeColorTypes.INSTANCE.foliage();
        }

        int resolvedColorType = VanadiumColorResolverCustomCompatibility.getcolorType(colorResolver);

        if (resolvedColorType >= localCache.blendedChunksCount) {
            localCache.reallocateBlendedChunkyArray(resolvedColorType);
        }

        return resolvedColorType;
    }

    @Inject(method = "clearTintCaches", at = @At("HEAD"))
    public void onColorCachesCleared(CallbackInfo ci) {
        vanadium$blendedColorCache.invalidateAllChunks();

        int blendingRadius = VanadiumBlendingConfiguration.getBiomeBlendingRadius();

        vanadium$chunkColorCache.invalidateAllCachesInRadius(blendingRadius);
    }

    @Inject(method="clearTintCaches", at=@At("RETURN"))
    private void reloadVanadiumColorCaches(CallbackInfo ci) {
        this.tintCaches.entrySet().removeIf(entry -> entry.getKey() instanceof ExtendedColorResolver);
    }

    @Inject(method = "onChunkLoaded", at = @At("HEAD"))
    public void onChunkLoadedHandled(ChunkPos chunkPos, CallbackInfo ci) {
        vanadium$blendedColorCache.invalidateChunk(chunkPos.x, chunkPos.z);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getBlockTint(BlockPos blockPosIn, ColorResolver colorResolverIn) {
        if(this.tintCaches.get(colorResolverIn) == null) {
            this.tintCaches.put(colorResolverIn, new BlockTintCache(pos1 -> this.calculateBlockTint(blockPosIn, colorResolverIn)));
        }

        final Coordinates positionedCoordinates = new Coordinates(blockPosIn.getX(), blockPosIn.getY(), blockPosIn.getZ());
        final Coordinates chunkCoordinates = new Coordinates(positionedCoordinates.x() >> 4, positionedCoordinates.y() >> 4, positionedCoordinates.z() >> 4);
        final Coordinates blockCoordinates = new Coordinates(positionedCoordinates.x() & 15, positionedCoordinates.y() & 15, positionedCoordinates.z() & 15);

        LocalCache localCache = vanadium$threadLocalCache.get();

        BlendingChunk chunk = null;
        int colorType;

        if (localCache.latestColorResolver == colorResolverIn) {
            colorType = localCache.lastColorType;
            long cachedChunkKey = ColorBlendingCache.getChunkCacheKey(chunkCoordinates, colorType);

            if (localCache.lastBlendedChunk.key == cachedChunkKey) {
                chunk = localCache.lastBlendedChunk;
            }
        } else {
            colorType = calculateColorType(colorResolverIn, localCache);

            long cachedChunkKey = ColorBlendingCache.getChunkCacheKey(chunkCoordinates, colorType);
            BlendingChunk cachedChunk = localCache.blendedChunks[colorType];
            if (localCache.lastBlendedChunk.key == cachedChunkKey) {
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
            ColorBlending.generateColors(
                    this,
                    colorResolverIn,
                    colorType,
                    vanadium$chunkColorCache,
                    chunk,
                    positionedCoordinates
            );

            color = chunk.data[index];
        }

        return color;
    }

    @ModifyArg(
            method="getSkyColor",
            at=@At(
                    value = "INVOKE",
                    target ="Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;"
            ),
            index = 1
    )
    private  CubicSampler.Vec3Fetcher proxySkyColor(CubicSampler.Vec3Fetcher sampler) {
        ResourceLocation dimensionId = Vanadium.getDimensionId(this);
        var resolver = BiomeColorMappings.getTotalSky(dimensionId);
        var biomeManager = this.getBiomeManager();
        var registryManager = this.registryAccess();
        return (x, y, z) -> {
            Registry<Biome> biomeRegistry = registryManager.registry(Registries.BIOME).get();
            var biome = Vanadium.getRegistryValue(biomeRegistry, biomeManager.getNoiseBiomeAtQuart(x,y,z));

            var quartCoordinates = new Coordinates(QuartPos.toBlock(x), QuartPos.toBlock(y), QuartPos.toBlock(z));

            return Vec3.fromRGB24(resolver.getColorAtCoordinatesForBiomeByManager(registryManager, biome, quartCoordinates));
        };
    }
}

