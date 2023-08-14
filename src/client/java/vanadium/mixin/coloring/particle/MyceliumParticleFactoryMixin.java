package vanadium.mixin.coloring.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SuspendParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;
import vanadium.utils.MathUtils;

@Mixin(SuspendParticle.MyceliumFactory.class)
public abstract class MyceliumParticleFactoryMixin {
    @Inject(method = "createParticle(Lnet/minecraft/particle/ParticleEffect;Lnet/minecraft/client/world/ClientWorld;DDDDDD)Lnet/minecraft/client/particle/Particle;",
    at = @At("RETURN"))
    private void onCreateParticle(CallbackInfoReturnable<Particle> cir) {
        if(Vanadium.MYCELIUM_PARTICLE_COLORS.hasCustomColorMapping()) {
            Particle particle = cir.getReturnValue();

            int color = Vanadium.MYCELIUM_PARTICLE_COLORS
                    .getRandomColorFromMapping();

            float red = ((color >> 16) & 0xff) * MathUtils.INV_255;
            float green = ((color >> 8) & 0xff) * MathUtils.INV_255;
            float blue = (color & 0xff) * MathUtils.INV_255;
            particle.setColor(red,green,blue);
        }
    }
}
