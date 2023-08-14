package vanadium.mixin.coloring.particle;

import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.Vanadium;
import vanadium.models.enums.ColoredParticle;
import vanadium.utils.MathUtils;

@Mixin(PortalParticle.class)
public abstract class PortalParticleMixin extends SpriteBillboardParticle {
    private PortalParticleMixin() {
        super(null, 0.0, 0.0, 0.0);
    }

    @Inject(method ="<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo ci) {
        int color = Vanadium.COLOR_PROPERTIES
                .getProperties()
                .getParticle(ColoredParticle.PORTAL);

        if(color != 0) {
            float multi = this.blue;
            this.red = multi * (((color >> 16) & 0xff) * MathUtils.INV_255);
            this.green = multi * (((color >> 8) & 0xff) * MathUtils.INV_255);
            this.blue = multi * ((color  & 0xff) * MathUtils.INV_255);
        }
    }
}
