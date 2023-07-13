package vanadium.mixin.blocks;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import me.jellysquid.mods.sodium.client.world.biome.BlockColorCache;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.biome.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockColorCache.class)
public class BlockColorCacheMixin {
    @Unique
    private int vanadiumBaseX;
    @Unique
    private int vanadiumBaseY;
    @Unique
    private int vanadiumBaseZ;
    @Unique
    private Reference2ReferenceOpenHashMap<ColorResolver, int[]> vanadiumColors;

    @Shadow
    private WorldSlice slice;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void constructorAtTail(CallbackInfo ci) {
        ChunkSectionPos pos = slice.getOrigin();

        this.vanadiumBaseX = pos.getMinX();
        this.vanadiumBaseY = pos.getMinY();
        this.vanadiumBaseZ = pos.getMinZ();

        this.vanadiumColors = new Reference2ReferenceOpenHashMap<>();
    }

    @Overwrite(remap = false)
    public int getColor(ColorResolver resolver, int x, int y, int z) {
        var colors = this.vanadiumColors.computeIfAbsent(resolver, k -> new int[4096]);
    }
}
