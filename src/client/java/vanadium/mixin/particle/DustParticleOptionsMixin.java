package vanadium.mixin.particle;

import net.minecraft.client.particle.DustParticle;
import net.minecraft.core.particles.DustParticleOptions;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.particle.CustomizedRedDustParticleEffect;

@Mixin(DustParticleOptions.class)
public abstract class DustParticleOptionsMixin {

    @Redirect(
            method = "<clinit>",
            at= @At(
                    value = "NEW",
                    target="(Lorg/joml/Vector3f;F)Lnet/minecraft/core/particles/DustParticleOptions;",
                    ordinal = 0
            )
    )
    private static DustParticleOptions proxyRedstoneDust(Vector3f color, float alpha) {
        return new CustomizedRedDustParticleEffect(color, alpha);
    }
}
