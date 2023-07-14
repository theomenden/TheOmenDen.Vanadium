package vanadium.mixin.client;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import me.jellysquid.mods.sodium.client.world.biome.BlockColorCache;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.ChunkSection;
import org.apache.commons.lang3.Range;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.util.ColorCacheUtils;

@Mixin(value = BlockColorCache.class)
public class BlockColorCacheMixin {
    @Unique
    private int vanadium$baseX;
    @Unique
    private int vanadium$baseY;
    @Unique
    private int vanadium$baseZ;
    @Unique
    private Reference2ReferenceOpenHashMap<ColorResolver, int[]> vanadiumColors;

    @Shadow
    private WorldSlice slice;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void constructorTail(WorldSlice slice, int radius, CallbackInfo ci) {
        ChunkSectionPos pos = slice.getOrigin();

        this.vanadium$baseX = pos.getX();
        this.vanadium$baseY = pos.getY();
        this.vanadium$baseZ = pos.getZ();

        this.vanadiumColors = new Reference2ReferenceOpenHashMap<>();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public int getColor(ColorResolver resolver, int x, int y, int z) {
        int[] colors = this.vanadiumColors.computeIfAbsent(resolver, k -> new int[4096]);

        int blockX = Range.between(0,15).fit(x - this.vanadium$baseX);
        int blockY = Range.between(0,15).fit(x - this.vanadium$baseY);
        int blockZ = Range.between(0,15).fit(x - this.vanadium$baseZ);

        int index = ColorCacheUtils.getArrayIndex(16, blockX, blockY, blockZ);

        int color = colors[index];

        if(color == 0) {
            BiomeAccess biomeManager = slice.getBiomeAccess();

            color = colors[index];
        }

        return color;
    }
}
