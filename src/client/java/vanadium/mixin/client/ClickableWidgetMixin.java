package vanadium.mixin.client;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import vanadium.Vanadium;

@Mixin(AbstractButton.class)
public abstract class ClickableWidgetMixin extends AbstractWidget {

    public ClickableWidgetMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @ModifyConstant(method = "renderWidget", constant = @Constant(intValue = 16777215))
    private int proxyButtonHoverColor(int original) {
        int col = Vanadium.COLOR_PROPERTIES.getProperties().getHoveredButtonText();
        return col != 0 && this.isHovered() ? col : original;
    }

    @ModifyConstant(method = "renderWidget", constant = @Constant(intValue = 10526880))
    private int proxyButtonDisabledColor(int original) {
        int col = Vanadium.COLOR_PROPERTIES.getProperties().getDisabledButtonText();
        return col != 0 ? col : original;
    }
}
