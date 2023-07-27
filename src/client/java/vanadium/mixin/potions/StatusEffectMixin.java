package vanadium.mixin.potions;

import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;

@Mixin(MobEffect.class)
public abstract class StatusEffectMixin {
    @Inject(
            method = "getColor",
            at=@At("HEAD"),
            cancellable = true
    )
    private void onGetColor(CallbackInfoReturnable<Integer> cir) {
        MobEffect self = (MobEffect)(Object)(this);

        int color = Vanadium.COLOR_PROPERTIES.getProperties().getPotion(self);
        if(color != 0) {
            cir.setReturnValue(color);
        }
    }
}
