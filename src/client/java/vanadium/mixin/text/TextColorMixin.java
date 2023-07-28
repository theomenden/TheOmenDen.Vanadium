package vanadium.mixin.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;
import vanadium.util.ColorConverter;

@Mixin(TextColor.class)
public abstract class TextColorMixin {
    @Inject(method = "parseColor",
    at = @At("HEAD"),
    cancellable = true,
    require = 1)
    private static void parseColor(String parsableColorString, CallbackInfoReturnable<TextColor> cir) {
        if(!parsableColorString.startsWith("#")) {
            return;
        }

        try {
            int parsedColor = ColorConverter.hexToRgbU(parsableColorString);
            cir.setReturnValue(TextColor.fromRgb(parsedColor));
        } catch (NumberFormatException nfe) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "parseColor",
    at = @At("RETURN"),
    cancellable = true)
    private static void switchToCustomColor(CallbackInfoReturnable<TextColor> cir) {
        if(cir.getReturnValue() == null) {
            return;
        }

        String name = cir.getReturnValue().formatValue();

        if(name == null) {
            return;
        }

        var formatting = ChatFormatting.getByName(name);

        if(formatting == null) {
            return;
        }

        TextColor color = Vanadium.COLOR_PROPERTIES.getProperties().getText(formatting);

        if(color != null) {
            cir.setReturnValue(color);
        }
    }
}
