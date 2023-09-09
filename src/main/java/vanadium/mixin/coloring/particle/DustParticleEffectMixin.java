package vanadium.mixin.coloring.particle;

import net.minecraft.core.particles.DustParticleOptions;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.customcolors.particle.CustomRedDustParticle;

@Mixin(DustParticleOptions.class)
public abstract class DustParticleEffectMixin {

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "NEW",
                    target = "(Lorg/joml/Vector3f;F)Lnet/minecraft/core/particles/DustParticleOptions;",
                    ordinal = 0
            )
    )
    private static DustParticleOptions proxyRedDust(Vector3f color, float a) {
        return new CustomRedDustParticle(color, a);
    }
}
