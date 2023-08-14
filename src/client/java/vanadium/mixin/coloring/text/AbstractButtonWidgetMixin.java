package vanadium.mixin.coloring.text;

import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import vanadium.Vanadium;

@Mixin(ClickableWidget.class)
public abstract class AbstractButtonWidgetMixin {

    @Shadow public abstract boolean isHovered();

    @ModifyConstant(method = "renderButton",
    constant = @Constant(intValue = 16777215))
    private int proxyRenderButton(int original) {
        int color = Vanadium.COLOR_PROPERTIES
                .getProperties()
                .getHoveredButtonText();

        return color != 0 && this.isHovered()
                ? color
                : original;
    }

    @ModifyConstant(method = "renderButton",
    constant=@Constant(intValue = 10526880))
    private int proxyDisabledButtonColor(int original) {
        int color = Vanadium.COLOR_PROPERTIES
                .getProperties()
                .getDisabledButtonText();

        return color != 0
                ? color
                : original;
    }

}
