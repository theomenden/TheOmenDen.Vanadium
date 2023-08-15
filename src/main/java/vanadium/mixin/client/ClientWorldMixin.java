package vanadium.mixin.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.world.BiomeColorCache;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;
import vanadium.biomeblending.blending.BlendingChunk;
import vanadium.biomeblending.caching.BlendingCache;
import vanadium.biomeblending.caching.ColorCache;
import vanadium.biomeblending.caching.LocalCache;
import vanadium.customcolors.ExtendedColorResolver;
import vanadium.customcolors.blending.ColorBlending;
import vanadium.customcolors.mapping.BiomeColorMappings;
import vanadium.models.NonBlockingThreadLocal;
import vanadium.models.records.BiomeColorTypes;
import vanadium.models.records.Coordinates;
import vanadium.utils.ColorCachingUtils;

import java.util.function.Supplier;

import static vanadium.biomeblending.compatibility.CustomColorCompatibility.getColorType;

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
    ),
    cancellable = true)
    private void calculateColorType(BlockPos pos, ColorResolver colorResolver, CallbackInfoReturnable<Integer> cir) {
      final Coordinates blockPositionCoordinates = new Coordinates(pos.getX(), pos.getY(), pos.getZ());
      final Coordinates chunkCoordinates = new Coordinates(blockPositionCoordinates.x() >> 4, blockPositionCoordinates.y() >> 4, blockPositionCoordinates.z() >> 4);
      final Coordinates blockCoordinates = new Coordinates(blockPositionCoordinates.x() & 15, blockPositionCoordinates.y() & 15, blockPositionCoordinates.z() & 15);

      LocalCache localCache = vanadiumBiomeBlending$threadLocalCache.get();

        BlendingChunk chunk = null;
        int colorType;

        if(localCache.latestColorResolver == colorResolver) {
            colorType = localCache.lastColorType;
            long key = ColorCachingUtils.getChunkKey(chunkCoordinates, colorType);

            if(localCache.lastBlendedChunk.key == key){
                chunk = localCache.lastBlendedChunk;
            }
        } else {
            if(colorResolver == BiomeColors.GRASS_COLOR) {
                colorType = BiomeColorTypes.INSTANCE.grass();
            } else if (colorResolver == BiomeColors.WATER_COLOR) {
                colorType = BiomeColorTypes.INSTANCE.water();
            } else if (colorResolver == BiomeColors.FOLIAGE_COLOR) {
                colorType = BiomeColorTypes.INSTANCE.foliage();
            } else {
                colorType = getColorType(colorResolver);

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

        if(chunk == null) {
            chunk = vanadium$blendingColorCache.getOrInitializeChunk(chunkCoordinates, colorType);
            localCache.putChunkInBlendedCache(vanadium$blendingColorCache, chunk, colorType, colorResolver);
        }

        int index = ColorCachingUtils.getArrayIndex(16, blockCoordinates);
        int color = chunk.data[index];

        if(color == 0) {
            ColorBlending.generateColors(this,
                    colorResolver,
                    colorType,
                    vanadium$chunkColorCache,
                    chunk,
                    blockPositionCoordinates);

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

@ModifyArg(
        method = "getSkyColor",
        at = @At(
                value = "INVOKE",
                target="Lnet/minecraft/util/CubicSampler;sampleColor(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/CubicSampler$RgbFetcher;)Lnet/minecraft/util/math/Vec3d;"
        )
)
    private CubicSampler.RgbFetcher proxySkyColor(CubicSampler.RgbFetcher original) {
        var dimensionId = Vanadium.getDimensionid(this);
        var resolver = BiomeColorMappings.getTotalSky(dimensionId);
        var biomeAccess = this.getBiomeAccess();
        var manager = this.getRegistryManager();

        return (x, y, z) -> {
            var biomeRegistry = manager.get(RegistryKeys.BIOME);
            var biome = Vanadium.getRegistryValue(biomeRegistry, biomeAccess.getBiomeForNoiseGen(x,y,z));
            return Vec3d.unpackRgb(resolver.getColorAtCoordinatesForBiome(manager, biome, new Coordinates(BiomeCoords.toBlock(x),BiomeCoords.toBlock(y),BiomeCoords.toBlock(z))));
        };
}

@Inject(method="getColor", at = @At("HEAD"))
    private void onGetColorHead(BlockPos pos, ColorResolver resolver, CallbackInfoReturnable<Integer> cir) {
        if(this.colorCache.get(resolver) == null) {
            this.colorCache.put(resolver, new BiomeColorCache(pos1 -> this.calculateColor(pos1, resolver)));
        }
    }

    @Inject(method="reloadColor", at = @At("RETURN"))
    private void reloadVanadiumColor(CallbackInfo ci){
        this.colorCache.entrySet()
                .removeIf(entry -> entry.getKey() instanceof ExtendedColorResolver);
    }

}
