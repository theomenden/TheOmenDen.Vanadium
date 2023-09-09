package vanadium.mixin.coloring.particle;

import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.models.enums.ColoredParticle;
import vanadium.utils.MathUtils;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(PortalParticle.class)
public abstract class PortalParticleMixin extends TextureSheetParticle {
    private PortalParticleMixin() {
        super(null, 0.0, 0.0, 0.0);
    }

    @Inject(method ="<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo ci) {
        var colorProperties = ObjectUtils.firstNonNull(
                VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                VanadiumColormaticResolution.COLOR_PROPERTIES
        );
        int color = colorProperties
                .getProperties()
                .getParticle(ColoredParticle.PORTAL);

        if(color != 0) {
            float multi = this.bCol;
            this.rCol = multi * (((color >> 16) & 0xff) * MathUtils.INV_255);
            this.gCol = multi * (((color >> 8) & 0xff) * MathUtils.INV_255);
            this.bCol = multi * ((color  & 0xff) * MathUtils.INV_255);
        }
    }
}
