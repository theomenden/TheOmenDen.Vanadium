package vanadium.mixin.particle;

import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.Vanadium;
import vanadium.enums.ColoredParticle;
import vanadium.util.MathUtils;

@Mixin(PortalParticle.class)
public abstract class PortalParticleMixin extends TextureSheetParticle {
    private PortalParticleMixin() {
        super(null, 0.0, 0.0, 0.0);
    }

    @Inject(method="<init>", at=@At("RETURN"))
    private void onConstructor(CallbackInfo ci) {
        int color = Vanadium.COLOR_PROPERTIES.getProperties().getParticle(ColoredParticle.PORTAL);

        if(color != 0) {
            float multiplex = this.bCol;

            this.rCol = multiplex * (((color >> 16) & 0xff) * MathUtils.INV_255);
            this.gCol = multiplex * (((color >> 8) & 0xff) * MathUtils.INV_255);
            this.bCol = multiplex * ((color & 0xff) * MathUtils.INV_255);
        }
    }
}
