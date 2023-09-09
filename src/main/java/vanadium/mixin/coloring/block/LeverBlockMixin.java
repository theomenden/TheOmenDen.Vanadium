package vanadium.mixin.coloring.block;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeverBlock;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.customcolors.particle.CustomRedDustParticle;

@Mixin(LeverBlock.class)
public abstract class LeverBlockMixin extends Block {
    private LeverBlockMixin(){super(null);}

    @Redirect(
            method="makeParticle",
            at = @At(
                    value = "NEW",
                    target="(Lorg/joml/Vector3f;F)Lnet/minecraft/core/particles/DustParticleOptions;",
                    ordinal = 0
            )
    )
    private static DustParticleOptions proxyRedDust(Vector3f color, float alpha) {
        return new CustomRedDustParticle(color, alpha);
    }
}
