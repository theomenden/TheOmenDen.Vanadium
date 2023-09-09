package vanadium.mixin.coloring.particle;

import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.utils.MathUtils;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(DripParticle.class)
public abstract class DrippingLavaParticleMixin extends TextureSheetParticle {

    @Unique
    private int age;

    private DrippingLavaParticleMixin() {
        super(null, 0.0,0.0,0.0);
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void onConstructor(CallbackInfo ci) {
        age = 0;
        if(VanadiumColormaticResolution.hasCustomLavaDropParticleColors()) {
            var lavaDropColors = ObjectUtils.firstNonNull(
                    VanadiumColormaticResolution.LAVA_DROP_COLORS,
                    VanadiumColormaticResolution.COLORMATIC_LAVA_DROP_COLORS
            );
            int color = lavaDropColors.getColorAtIndex(0);

            float red = ((color >> 16) & 0xff) * MathUtils.INV_255;
            float green = ((color >> 8) & 0xff) * MathUtils.INV_255;
            float blue = (color & 0xff) * MathUtils.INV_255;
            this.setColor(red, green, blue);
        }
    }

    @Inject(
            method="preMoveUpdate",
            at = @At(
                    value = "RETURN",
                    shift = At.Shift.BY,
                    by = -2
            )
    )
    private void onUpdateAge(CallbackInfo ci) {
        if(VanadiumColormaticResolution.hasCustomLavaDropParticleColors()) {
            var lavaDropColors = ObjectUtils.firstNonNull(
                    VanadiumColormaticResolution.LAVA_DROP_COLORS,
                    VanadiumColormaticResolution.COLORMATIC_LAVA_DROP_COLORS
            );
            int color = lavaDropColors.getColorAtIndex(++age);
            float red = ((color >> 16) & 0xff) * MathUtils.INV_255;
            float green = ((color >> 8) & 0xff) * MathUtils.INV_255;
            float blue = (color & 0xff) * MathUtils.INV_255;
            this.setColor(red, green, blue);
        }
    }
}
