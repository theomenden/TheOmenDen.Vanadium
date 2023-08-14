package vanadium.mixin.coloring.potions;

import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;

@Mixin(StatusEffect.class)
public abstract class StatusEffectMixin {
    @Inject(method = "getColor",
    at = @At("HEAD"),
    cancellable = true)
    private void onColor(CallbackInfoReturnable<Integer> cir) {
        StatusEffect self = (StatusEffect) (Object)this;
        int color = Vanadium.COLOR_PROPERTIES
                .getProperties()
                .getPotion(self);

        if(color != 0) {
            cir.setReturnValue(color);
        }
    }
}
