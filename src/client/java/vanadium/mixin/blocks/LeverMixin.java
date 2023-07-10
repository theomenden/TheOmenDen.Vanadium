package vanadium.mixin.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.LeverBlock;
import net.minecraft.particle.DustParticleEffect;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LeverBlock.class)
public abstract class LeverMixin extends Block {
    private LevelMixin() {
        super(null);
    }

    @Redirect(method = "spawnParticles",
    at = @At(
            value = "NEW",
            target = "net/minecraft/particle/DustParticleEffect",
            oridinal = 0
    ))
    private static DustParticleEffect redDustProxy(Vector3f color, float alpha) {
        return new CustomColoredRedDustParticleEffect(color, alpha);
    }
}
