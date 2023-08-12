package vanadium.mixin.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.world.BiomeColorCache;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.VanadiumClient;
import vanadium.biomeblending.caching.LocalCache;
import vanadium.models.NonBlockingThreadLocal;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World {
    @Shadow
    private final Object2ObjectArrayMap<ColorResolver, BiomeColorCache> colorCache = new Object2ObjectArrayMap<>();

    @Unique
    private final ThreadLocal<LocalCache> vanadiumBiomeBlending$threadLocalCache = NonBlockingThreadLocal.withInitial(LocalCache::new);


    @Inject(method="reloadColor", at = @At("HEAD"))
    public void onClearColorCache(CallbackInfo ci) {
        vanadiumBiomeBlending$blendingColorCache.invalidateAll();
        int blendingRadius = VanadiumClient.getBiomeBlendingRadius();
        vanadiumBiomeBlending$ChunkColorCahce.invalidateAllInRadius(blendingRadius);
    }

    @Inject(method="resetChunkColor", at = @At("HEAD"))
    public void onResetChunkColor(ChunkPos chunkPos, CallbackInfo ci) {
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;

        vanadiumBiomeBlending$BlendingColorCache.invalidateChunkAt(chunkX, chunkZ);
    }

    
}
