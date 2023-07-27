package vanadium.mixin.block;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeverBlock;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.particle.CustomizedRedDustParticleEffect;

@Mixin(LeverBlock.class)
public abstract class LeverMixin extends Block {
    private LeverMixin() {
        super(null);
    }

    @Redirect(
            method = "makeParticle",
            at = @At(
                    value="NEW",
            target = "(Lorg/joml/Vector3f;F)Lnet/minecraft/core/particles/DustParticleOptions;",
            ordinal = 0
        )
    )
    private static DustParticleOptions redDustProxy(Vector3f color, float alpha) {
        return new CustomizedRedDustParticleEffect(color, alpha);
    }


}
