package vanadium.mixin.sodium;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.jellysquid.mods.sodium.client.world.biome.BiomeColorCache;
import me.jellysquid.mods.sodium.client.world.biome.BiomeColorSource;
import me.jellysquid.mods.sodium.client.world.biome.BiomeSlice;
import me.jellysquid.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.world.biome.ColorResolver;
import org.apache.commons.lang3.Range;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.models.records.Coordinates;
import vanadium.utils.ColorCachingUtils;

@Mixin(BiomeColorCache.class)
public abstract class SodiumBiomeColorCacheMixin {
    @Shadow private int minX;
    @Shadow private int minY;
    @Shadow private int minZ;
    @Unique
   private Coordinates vanadium$blendingMinimums;

   @Unique
   private Reference2ReferenceMap<ColorResolver, int[]> vanadium$blendingColors = new Reference2ReferenceOpenHashMap<>();

   @Inject(
           method = "<init>",
           at = @At("TAIL")
   )
   public void onConstructorTail(BiomeSlice slice, int blendingRadius, CallbackInfo ci) {
       vanadium$blendingColors = new Reference2ReferenceOpenHashMap<>();
   }

    @Inject(method = "update(Lme/jellysquid/mods/sodium/client/world/cloned/ChunkRenderContext;)V", at=@At("TAIL"), remap = false)
    private void onUpdateHead(ChunkRenderContext context, CallbackInfo ci) {
        var origin = context.getOrigin();
        this.vanadium$blendingMinimums = new Coordinates(origin.getMinX(), origin.getMinY(), origin.getMinZ());
    }


    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public int getColor(BiomeColorSource biomeColorSource, int posX, int posY, int posZ) {

       if(this.vanadium$blendingMinimums == null) {
           this.vanadium$blendingMinimums = new Coordinates(this.minX, this.minY, this.minZ);
       }

        var resolver = getResolver(biomeColorSource);

        int[] colors = this.vanadium$blendingColors.computeIfAbsent(resolver, k -> new int[4096]);
        int blockX = Range.between(0,15).fit(posX - this.vanadium$blendingMinimums.x());
        int blockY = Range.between(0,15).fit(posY - this.vanadium$blendingMinimums.y());
        int blockZ = Range.between(0,15).fit(posZ - this.vanadium$blendingMinimums.z());

        int index = ColorCachingUtils.getArrayIndex(16, blockX, blockY, blockZ);

        return colors[index];
    }

    @Unique
    private ColorResolver getResolver(BiomeColorSource colorSource) {
        return switch(colorSource) {
            case GRASS -> BiomeColors.GRASS_COLOR;
            case FOLIAGE -> BiomeColors.FOLIAGE_COLOR;
            case WATER -> BiomeColors.WATER_COLOR;
        };
    }
}
