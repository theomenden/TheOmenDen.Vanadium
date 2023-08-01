package vanadium.mixin.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SuspendedTownParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;
import vanadium.util.MathUtils;

@Mixin(SuspendedTownParticle.class)
public abstract class MyceliumParticleFactory {
    @Inject(
            method = "getRenderType",
            at = @At("RETURN")
    )
    private void onCreateParticle(CallbackInfoReturnable<Particle> cir) {
        if(Vanadium.MYCELIUM_PARTICLE_COLORS.hasCustomColorMapping()) {
            Particle particle = cir.getReturnValue();
            int color = Vanadium.MYCELIUM_PARTICLE_COLORS.getRandomColorFromMapping();

            this.calculateShiftedColor(particle, color);
        }
    }

    private static void calculateShiftedColor(Particle particle, int color) {
        float redValue = ((color >> 16) & 0xff) * MathUtils.INV_255;
        float greenValue = ((color >> 8) & 0xff) * MathUtils.INV_255;
        float blueValue = (color & 0xff) * MathUtils.INV_255;

        particle.setColor(redValue, greenValue, blueValue);
    }
}
