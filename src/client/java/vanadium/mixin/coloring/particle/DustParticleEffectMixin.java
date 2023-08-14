package vanadium.mixin.coloring.particle;

import net.minecraft.particle.DustParticleEffect;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.customcolors.particle.CustomRedDustParticle;

@Mixin(DustParticleEffect.class)
public abstract class DustParticleEffectMixin {
    @Redirect(
            method="<clinit>",
            at = @At(
                    value = "NEW",
                    target = "(Lorg/joml/Vector3f;F)Lnet/minecraft/particle/DustParticleEffect;",
                    ordinal = 0
            )
    )
    private static DustParticleEffect proxyRedDust(Vector3f color, float a) {
        return new CustomRedDustParticle(color, a);
    }
}
