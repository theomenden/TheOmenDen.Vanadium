package vanadium.mixin.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import me.jellysquid.mods.sodium.client.world.biome.BlockColorCache;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.client.color.block.BlockColorProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.blending.BlendingConfig;
import vanadium.caching.BlendingCache;
import vanadium.caching.ColorCache;
import vanadium.caching.LocalCache;

import java.util.function.Supplier;

@Mixin(value = ClientWorld.class)
public abstract class ClientWorldMixin extends World {

    @Shadow
    private final Object2ObjectArrayMap<ColorResolver, BlockColorCache> colorCache = new Object2ObjectArrayMap<>();
    
    @Unique
    private final BlendingCache blendedColorCache = new BlendingCache(1024);

    @Unique
    public final ColorCache chunkColorCache = new ColorCache(1024);

    @Unique
    private final ThreadLocal<LocalCache> threadLocalCache = ThreadLocal.withInitial(LocalCache::new);

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
    public void onClearColorCaches(CallbackInfo ci) {
        blendedColorCache.invalidateAllChunks();

        int blendingRadius = VanadiumBlendClient.getBiomeBlendingRadius();
        chunkColorCache.invalidateAllCachesInRadius(blendingRadius);
    }

    @Inject(method = "isChunkLoaded", at = @At("HEAD"))
    public void isChunkLoaded(int x, int z, CallbackInfoReturnable<Boolean> cir) {
        if (this.getChunkManager().isChunkLoaded(x, z)) {
            cir.setReturnValue(true);


        }
    }
}
