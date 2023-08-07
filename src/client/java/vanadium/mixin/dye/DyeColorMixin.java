package vanadium.mixin.dye;

import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.entry.Vanadium;

@Mixin(DyeColor.class)
public abstract class DyeColorMixin {
    @Inject(method = "getTextColor",
    at=@At("HEAD"),
    cancellable = true)
    private void onSignColor(CallbackInfoReturnable<Integer> cir) {
        int color = Vanadium.COLOR_PROPERTIES.getProperties().getSignText((DyeColor) (Object)(this));

        if(color != 0) {
            cir.setReturnValue(color);
        }
    }
}
