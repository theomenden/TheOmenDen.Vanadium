package vanadium.mixin.coloring.particle;

import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.Particle;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.utils.MathUtils;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(DripParticle.class)
public abstract class LandingLavaParticleFactoryMixin {

    @Inject(method ="createLavaLandParticle", at = @At("RETURN"))
    private static void onCreateLandingLava(CallbackInfoReturnable<Particle> cir) {
        if(VanadiumColormaticResolution.hasCustomLavaDropParticleColors()) {
            Particle particle = cir.getReturnValue();
            var lavaDropColors = ObjectUtils.firstNonNull(
                    VanadiumColormaticResolution.LAVA_DROP_COLORS,
                    VanadiumColormaticResolution.COLORMATIC_LAVA_DROP_COLORS
            );
            int color = lavaDropColors
                    .getColorAtIndex(Integer.MAX_VALUE);

            float red = ((color >> 16) & 0xff) * MathUtils.INV_255;
            float green = ((color >> 8) & 0xff) * MathUtils.INV_255;
            float blue = (color & 0xff) * MathUtils.INV_255;
            particle.setColor(red, green, blue);
        }
    }
}
