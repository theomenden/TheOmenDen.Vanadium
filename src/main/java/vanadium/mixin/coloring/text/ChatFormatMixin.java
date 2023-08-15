package vanadium.mixin.coloring.text;

import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;

@Mixin(Style.class)
public abstract class ChatFormatMixin {
    @Inject(method="getColor",
    at = @At("RETURN"),
    cancellable = true)
    private void useCustomTextColor(CallbackInfoReturnable<TextColor> cir){
        if(cir.getReturnValue() != null) {
            String name = cir.getReturnValue()
                             .getName();

            if(name != null) {
                Formatting formatting = Formatting.byName(name);

                if(formatting != null) {
                    TextColor color = Vanadium.COLOR_PROPERTIES
                            .getProperties()
                            .getText(formatting);

                    if(color != null) {
                        cir.setReturnValue(color);
                    }
                }
            }
        }
    }
}
