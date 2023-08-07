package vanadium.mixin.particle;

import net.minecraft.client.particle.LavaParticle;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.entry.Vanadium;
import vanadium.util.MathUtils;

@Mixin(LavaParticle.Provider.class)
public abstract class FallingLavaParticleMixin {
    @Inject(method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)Lnet/minecraft/client/particle/Particle;",
    at = @At("RETURN"))
    private void onCreateParticle(CallbackInfoReturnable<Particle> cir) {
        if(Vanadium.LAVA_DROP_COLORS.hasCustomColorMapping()) {
            Particle particle = cir.getReturnValue();

            int color = Vanadium.LAVA_DROP_COLORS.getColorAtIndex(Integer.MAX_VALUE);

            calculateShiftedColor(particle, color);
        }
    }

    private static void calculateShiftedColor(Particle particle, int color) {
        float redValue = ((color >> 16) & 0xff) * MathUtils.INV_255;
        float greenValue = ((color >> 8) & 0xff) * MathUtils.INV_255;
        float blueValue = (color & 0xff) * MathUtils.INV_255;

        particle.setColor(redValue, greenValue, blueValue);
    }
}
