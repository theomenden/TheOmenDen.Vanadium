package vanadium.mixin.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.particle.RedDustParticleCustomColorizer;

@Mixin(LeverBlock.class)
public abstract class LeverMixin extends Block {
    private LeverMixin() {
        super(null);
    }

    @Redirect(
            method = "spawnParticles",
    at =  @At(
            value = "NEW",
            target = "(Lorg/joml/Vector3f;F)Lnet/minecraft/particle/DustParticleEffect;",
            ordinal = 0
        )
    )
     private static DustParticleEffect proxyRedDustParticleEffect(Vector3f color, float scale) {
       return new RedDustParticleCustomColorizer(color, scale);
    }

}