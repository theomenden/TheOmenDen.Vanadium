package vanadium.mixin.coloring.potions;

import net.minecraft.world.effect.MobEffect;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(MobEffect.class)
public abstract class StatusEffectMixin {
    @Inject(method = "getColor",
    at = @At("HEAD"),
    cancellable = true)
    private void onColor(CallbackInfoReturnable<Integer> cir) {
        MobEffect self = (MobEffect) (Object)this;
        var colorProperties = ObjectUtils.firstNonNull(
                VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                VanadiumColormaticResolution.COLOR_PROPERTIES
        );
        int color = colorProperties
                .getProperties()
                .getPotion(self);

        if(color != 0) {
            cir.setReturnValue(color);
        }
    }
}
