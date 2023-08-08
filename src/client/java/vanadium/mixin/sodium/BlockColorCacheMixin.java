package vanadium.mixin.sodium;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.jellysquid.mods.sodium.client.world.biome.BiomeColorCache;
import me.jellysquid.mods.sodium.client.world.biome.BiomeColorSource;
import me.jellysquid.mods.sodium.client.world.biome.BiomeSlice;
import me.jellysquid.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.ColorResolver;
import org.apache.commons.lang3.Range;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.util.ColorCacheUtils;

@Mixin(BiomeColorCache.class)
public class BlockColorCacheMixin {
    @Shadow @Final private BiomeSlice biomeData;
    @Shadow private int minX;
    @Shadow private int minY;
    @Shadow private int minZ;
    @Unique private int vanadium$baseX;
    @Unique private int vanadium$baseY;
    @Unique private int vanadium$baseZ;

 @Unique private Reference2ReferenceOpenHashMap<ColorResolver, int[]> vanadium$colors;

 @Inject(method = "<init>", at=@At("TAIL"))
 public void constructorTail(BiomeSlice biomeData, int blendRadius, CallbackInfo ci){
     this.vanadium$colors = new Reference2ReferenceOpenHashMap<>();
 }

 @Inject(method = "update", at =  @At("TAIL"))
 public void update(ChunkRenderContext context, CallbackInfo ci) {
     vanadium$baseX = this.minX;
     vanadium$baseY = this.minY;
     vanadium$baseZ = this.minZ;
 }

 /**
  * @author
  * @reason
  */
    @Overwrite(remap = false)
    public int getColor(BiomeColorSource biomeColorSource, int posX, int posY, int posZ) {

        var resolver = BiomeColors.FOLIAGE_COLOR_RESOLVER.getColor();

     int[] colors = this.vanadium$colors.computeIfAbsent(resolver, k -> new int[4096]);

     int blockX = Range.between(0,15).fit(posX - this.vanadium$baseX);
     int blockY = Range.between(0,15).fit(posX - this.vanadium$baseY);
     int blockZ = Range.between(0,15).fit(posX - this.vanadium$baseZ);

     int index = ColorCacheUtils.getArrayIndex(16, blockX, blockY, blockZ);

     int color = colors[index];

     if(color == 0) {

     }
 }

}
