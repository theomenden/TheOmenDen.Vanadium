package vanadium.mixin.coloring.particle;

import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.Vanadium;
import vanadium.utils.MathUtils;

@Mixin(BlockLeakParticle.class)
public abstract class DrippingLavaParticleMixin extends SpriteBillboardParticle {

    @Unique
    private int age;

    private DrippingLavaParticleMixin() {
        super(null, 0.0,0.0,0.0);
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void onConstructor(CallbackInfo ci) {
        age = 0;
        if(Vanadium.LAVA_DROP_COLORS.hasCustomColorMapping()) {
            int color = Vanadium.LAVA_DROP_COLORS.getColorAtIndex(0);

            float red = ((color >> 16) & 0xff) * MathUtils.INV_255;
            float green = ((color >> 8) & 0xff) * MathUtils.INV_255;
            float blue = (color & 0xff) * MathUtils.INV_255;
            this.setColor(red, green, blue);
        }
    }

    @Inject(
            method="updateAge",
            at = @At(
                    value = "RETURN",
                    shift = At.Shift.BY,
                    by = -2
            )
    )
    private void onUpdateAge(CallbackInfo ci) {
        if(Vanadium.LAVA_DROP_COLORS.hasCustomColorMapping()) {
            int color = Vanadium.LAVA_DROP_COLORS.getColorAtIndex(++age);
            float red = ((color >> 16) & 0xff) * MathUtils.INV_255;
            float green = ((color >> 8) & 0xff) * MathUtils.INV_255;
            float blue = (color & 0xff) * MathUtils.INV_255;
            this.setColor(red, green, blue);
        }
    }
}
