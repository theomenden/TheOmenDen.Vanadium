package vanadium.mixin.coloring.particle;

import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;
import vanadium.utils.MathUtils;

@Mixin(BlockLeakParticle.class)
public abstract class FallingLavaParticleFactoryMixin {
    @Inject(method = "createFallingLava", at = @At("RETURN"))
    private static void onCreateParticle(CallbackInfoReturnable<Particle> cir) {
        if(Vanadium.LAVA_DROP_COLORS.hasCustomColorMapping()) {
            Particle particle = cir.getReturnValue();
            int color = Vanadium.LAVA_DROP_COLORS.getColorAtIndex(Integer.MAX_VALUE);
            float red = ((color >> 16) & 0xff) * MathUtils.INV_255;
            float green = ((color >> 8) & 0xff) * MathUtils.INV_255;
            float blue = (color & 0xff) * MathUtils.INV_255;
            particle.setColor(red, green, blue);
        }
    }
}
