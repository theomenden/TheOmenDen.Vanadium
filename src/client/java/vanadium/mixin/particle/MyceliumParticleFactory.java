package vanadium.mixin.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SuspendedParticle;
import net.minecraft.client.particle.SuspendedTownParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;
import vanadium.util.MathUtils;

@Mixin(SuspendedTownParticle.Provider.class)
public abstract class MyceliumParticleFactory {
    @Inject(
            method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At("RETURN")
    )
    private void onCreateParticle(CallbackInfoReturnable<Particle> cir) {
        if(Vanadium.MYCELIUM_PARTICLE_COLORS.hasCustomColorMapping()) {
            Particle particle = cir.getReturnValue();
            int color = Vanadium.MYCELIUM_PARTICLE_COLORS.getRandomColorFromMapping();

            float rCol = ((color >> 16) & 0xff) * MathUtils.INV_255;
            float gCol = ((color >> 8) & 0xff) * MathUtils.INV_255;
            float bCol = (color  & 0xff) * MathUtils.INV_255;

            particle.setColor(rCol, gCol, bCol);
        }
    }
}
