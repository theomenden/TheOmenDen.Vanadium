package vanadium.mixin.coloring.particle;

import net.minecraft.client.particle.SplashParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.customcolors.mapping.BiomeColorMapping;
import vanadium.utils.MathUtils;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(SplashParticle.class)
public abstract class WaterSplashParticleMixin extends TextureSheetParticle {

    @Unique
    private static final BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos();
    private WaterSplashParticleMixin() {
        super(null, 0.0, 0.0, 0.0);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo ci) {
        if(!VanadiumColormaticResolution.hasCustomWaterColors()) {
            return;
        }

        BiomeColorMapping colorMapping = ObjectUtils.firstNonNull(VanadiumColormaticResolution.WATER_COLORS.getColorMapping(), VanadiumColormaticResolution.COLORMATIC_WATER_COLORS.getColorMapping());

        position.set(this.x, this.y, this.z);

        int color = BiomeColorMapping.getBiomeCurrentColorOrDefault(this.level, position, colorMapping);

        this.rCol = ((color >> 16) & 0xff) * MathUtils.INV_255;
        this.gCol = ((color >> 8) & 0xff) * MathUtils.INV_255 ;
        this.bCol = (color & 0xff) * MathUtils.INV_255;
    }
}
