package vanadium.mixin.particle;

import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.entry.Vanadium;
import vanadium.util.MathUtils;

@Mixin(DripParticle.class)
public abstract class DrippingLavaParticleMixin extends TextureSheetParticle {
    private DrippingLavaParticleMixin() {
        super(null, 0.0,0.0,0.0);
    }

    @Unique private int age;

    @Inject(method = "createDripstoneLavaFallParticle",
    at=@At("RETURN"))
    private static void onCreateParticle(CallbackInfoReturnable<DripParticle.DripstoneFallAndLandParticle> ci) {
        if(Vanadium.LAVA_DROP_COLORS.hasCustomColorMapping()) {
            int color = Vanadium.LAVA_DROP_COLORS.getColorAtIndex(0);

            var particle = ci.getReturnValue();
            float r = ((color >> 16) & 0xff) * MathUtils.INV_255;
            float g = ((color >> 8) & 0xff) * MathUtils.INV_255;
            float b = (color & 0xff) * MathUtils.INV_255;

            particle.setColor(r, g, b);

        }
    }

    @Inject(
            method = "preMoveUpdate",
            at = @At(value="RETURN", shift=At.Shift.BY, by = -2)
    )
    private void onLifetimeAdjusted(CallbackInfo ci) {
        if(Vanadium.LAVA_DROP_COLORS.hasCustomColorMapping()) {
            int color = Vanadium.LAVA_DROP_COLORS.getColorAtIndex(++this.lifetime);
            float r = ((color >> 16) & 0xff) * MathUtils.INV_255;
            float g = ((color >> 8) & 0xff) * MathUtils.INV_255;
            float b = (color & 0xff) * MathUtils.INV_255;

            this.setColor(r,g,b);
        }
    }
}
