package vanadium.mixin.coloring.block;

import net.minecraft.block.Block;
import net.minecraft.block.LeverBlock;
import net.minecraft.particle.DustParticleEffect;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.customcolors.particle.CustomRedDustParticle;

@Mixin(LeverBlock.class)
public abstract class LeverBlockMixin extends Block {
    private LeverBlockMixin(){super(null);}

    @Redirect(
            method = "spawnParticles",
            at = @At(
                    value = "NEW",
                    target="(Lorg/joml/Vector3f;F)Lnet/minecraft/particle/DustParticleEffect;",
                    ordinal = 0
            )
    )
            private static DustParticleEffect proxyRedDust(Vector3f color, float a) {
                return new CustomRedDustParticle(color, a);
    }
}
