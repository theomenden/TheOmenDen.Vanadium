package vanadium.mixin.coloring.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(Style.class)
public abstract class ChatFormatMixin {
    @Inject(method="getColor",
    at = @At("RETURN"),
    cancellable = true)
    private void useCustomTextColor(CallbackInfoReturnable<TextColor> cir){
        if(cir.getReturnValue() != null) {
            String name = cir.getReturnValue()
                             .toString();

            if(name != null) {
                ChatFormatting formatting = ChatFormatting.getByName(name);

                if(formatting != null) {
                    var colorProperties = ObjectUtils.firstNonNull(
                            VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                            VanadiumColormaticResolution.COLOR_PROPERTIES
                    );
                    TextColor color = colorProperties
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
