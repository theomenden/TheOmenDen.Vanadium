package vanadium.mixin.biome;

import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.customcolors.mapping.BiomeColorMapping;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(BiomeColors.class)
public abstract class BiomeColorsMixin {
    @Inject(method = "getWaterColor", at  = @At("HEAD"), cancellable = true)
    private static void onColoringWater(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if(VanadiumColormaticResolution.hasCustomWaterColors()) {
            var colormap = ObjectUtils.firstNonNull(
                    VanadiumColormaticResolution.WATER_COLORS.getColorMapping(),
                    VanadiumColormaticResolution.COLORMATIC_WATER_COLORS.getColorMapping());
            cir.setReturnValue(BiomeColorMapping.getBiomeCurrentColorOrDefault(world, pos, colormap));
        }
    }
}
