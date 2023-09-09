package vanadium.mixin.coloring.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SuspendedTownParticle;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.utils.MathUtils;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(SuspendedTownParticle.Provider.class)
public abstract class MyceliumParticleFactoryMixin {
    @Inject(method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)Lnet/minecraft/client/particle/Particle;",
    at = @At("RETURN"))
    private void onCreateParticle(CallbackInfoReturnable<Particle> cir) {
        if(VanadiumColormaticResolution.hasCustomMyceliumParticleColors()) {
            Particle particle = cir.getReturnValue();

            int color = ObjectUtils.firstNonNull(
                    VanadiumColormaticResolution.MYCELIUM_PARTICLE_COLORS.getRandomColorFromMapping(),
                    VanadiumColormaticResolution.COLORMATIC_MYCELIUM_PARTICLE_COLORS.getRandomColorFromMapping());

            float red = ((color >> 16) & 0xff) * MathUtils.INV_255;
            float green = ((color >> 8) & 0xff) * MathUtils.INV_255;
            float blue = (color & 0xff) * MathUtils.INV_255;
            particle.setColor(red,green,blue);
        }
    }
}
