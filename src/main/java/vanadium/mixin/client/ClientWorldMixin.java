package vanadium.mixin.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.world.BiomeColorCache;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.biome.source.BiomeCoords;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;
import vanadium.biomeblending.caching.BlendingCache;
import vanadium.biomeblending.caching.ColorCache;
import vanadium.biomeblending.caching.LocalCache;
import vanadium.customcolors.VanadiumExtendedColorResolver;
import vanadium.customcolors.mapping.BiomeColorMappings;
import vanadium.models.NonBlockingThreadLocal;
import vanadium.models.records.Coordinates;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World {
    @Final
    @Shadow
    private Object2ObjectArrayMap<ColorResolver, BiomeColorCache> colorCache = new Object2ObjectArrayMap<>();

    @Shadow
    public abstract int calculateColor(BlockPos pos, ColorResolver colorResolver);

    @Unique
    public final ColorCache vanadium$chunkColorCache = new ColorCache(1024);
    @Unique
    private final ThreadLocal<LocalCache> vanadium$threadLocalCache = NonBlockingThreadLocal.withInitial(LocalCache::new);

    @Unique
    private final BlendingCache vanadium$blendingColorCache = new BlendingCache(1024);

    private ClientWorldMixin() {
        super(null, null, null, null, null, false, false, 0L, 0);
    }


    @Inject(
            method = "getColor",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onGetColor(BlockPos pos, ColorResolver colorResolver, CallbackInfoReturnable<Integer> cir) {
        if(this.colorCache.get(colorResolver) == null) {
            this.colorCache.put(colorResolver, new BiomeColorCache(pos1 -> this.calculateColor(pos1, colorResolver)));
        }
        /*
        final Coordinates blockPosCoordinates = new Coordinates(pos.getX(), pos.getY(), pos.getZ());
        final Coordinates chunkCoordinates = new Coordinates(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
        final Coordinates blockCoordinates = new Coordinates(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);

        LocalCache localCache = vanadium$threadLocalCache.get();
        BlendingChunk chunk = null;
        int colorType;

        if(localCache.latestColorResolver == colorResolver) {
            colorType = localCache.lastColorType;
            long key = ColorCachingUtils.getChunkKey(chunkCoordinates,colorType);

            if(localCache.lastBlendedChunk.key == key) {
                chunk = localCache.lastBlendedChunk;
            }
        } else {
            if(colorResolver == BiomeColors.GRASS_COLOR) {
                colorType = BiomeColorTypes.INSTANCE.grass();
            }
            else if(colorResolver == BiomeColors.WATER_COLOR) {
                colorType = BiomeColorTypes.INSTANCE.water();
            }
            else if(colorResolver == BiomeColors.FOLIAGE_COLOR) {
                colorType = BiomeColorTypes.INSTANCE.foliage();
            }
            else {
                colorType = CustomColorCompatibility.getColorType(colorResolver);

                if(colorType >= localCache.blendedChunksCount) {
                    localCache.reallocateBlendedChunkyArray(colorType);
                }
            }

            long key = ColorCachingUtils.getChunkKey(chunkCoordinates, colorType);

            BlendingChunk cachedChunk = localCache.blendedChunks[colorType];

            if(cachedChunk.key == key) {
                chunk = cachedChunk;
            }
        }

        DebugUtils.countThreadLocalChunks(chunk);

        if(chunk == null) {
            chunk = vanadium$blendingColorCache.getOrInitializeChunk(chunkCoordinates, colorType);

            localCache.putChunkInBlendedCache(vanadium$blendingColorCache, chunk, colorType, colorResolver);
        }

        int index = ColorCachingUtils.getArrayIndex(16, blockCoordinates);

        int color = chunk.data[index];

        if(color == 0) {
            ColorBlending.generateColors(
                    this,
                    colorResolver,
                    colorType,
                    vanadium$chunkColorCache,
                    chunk,
                    blockPosCoordinates
            );
        }

        cir.setReturnValue(color);
         */
    }


    @Inject(method = "reloadColor", at = @At("HEAD"))
    public void onClearColorCache(CallbackInfo ci) {
        vanadium$blendingColorCache.invalidateAllChunks();
        int blendingRadius = Vanadium.configuration.blendingRadius;
        vanadium$chunkColorCache.invalidateAllCachesInRadius(blendingRadius);
    }

    @Inject(method = "reloadColor", at = @At("HEAD"))
    private void reloadVanadiumColor(CallbackInfo ci) {
        this.colorCache
                .entrySet()
                .removeIf(entry -> entry.getKey() instanceof VanadiumExtendedColorResolver);
    }

    @Inject(method = "resetChunkColor", at = @At("HEAD"))
    public void onResetChunkColor(ChunkPos chunkPos, CallbackInfo ci) {
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;
        vanadium$blendingColorCache.invalidateChunk(chunkX, chunkZ);
    }

    @ModifyArg(
            method = "getSkyColor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/CubicSampler;sampleColor(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/CubicSampler$RgbFetcher;)Lnet/minecraft/util/math/Vec3d;"
            )
    )
    private CubicSampler.RgbFetcher proxySkyColor(CubicSampler.RgbFetcher original) {
        var dimensionId = Vanadium.getDimensionid(this);
        var resolver = BiomeColorMappings.getTotalSky(dimensionId);
        var biomeAccess = this.getBiomeAccess();
        var manager = getRegistryManager();

        return (x, y, z) -> {
            var biomeRegistry = manager.get(RegistryKeys.BIOME);
            var biome = Vanadium.getRegistryValue(biomeRegistry, biomeAccess.getBiomeForNoiseGen(x, y, z));
            var biomeCoordinates = new Coordinates(BiomeCoords.toBlock(x), BiomeCoords.toBlock(y), BiomeCoords.toBlock(z));
            return Vec3d.unpackRgb(resolver.getColorAtCoordinatesForBiome(manager, biome, biomeCoordinates));
        };
    }
}

