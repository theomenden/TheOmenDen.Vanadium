package vanadium.mixin.coloring.dye;

import net.minecraft.world.item.DyeColor;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(DyeColor.class)
public abstract class DyeColorMixin {
    @Inject(
            method = "getTextColor",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onSignColor(CallbackInfoReturnable<Integer> cir) {
        var colorProperties = ObjectUtils.firstNonNull(
                VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                VanadiumColormaticResolution.COLOR_PROPERTIES
        );
        int color = colorProperties
                .getProperties()
                .getSignText((DyeColor) (Object)this);

        if(color != 0) {
            cir.setReturnValue(color);
        }
    }
}
