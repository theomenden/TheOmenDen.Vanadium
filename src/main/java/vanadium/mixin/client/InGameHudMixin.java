package vanadium.mixin.client;

import net.minecraft.client.gui.Gui;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(Gui.class)
public abstract class InGameHudMixin {
    @ModifyConstant(
            method = "renderExperienceBar",
            constant = @Constant(intValue = 8453920)
    )
    private int getExperienceTextColor(int original) {
        var xpTextProperties = ObjectUtils.firstNonNull(
                VanadiumColormaticResolution.COLOR_PROPERTIES,
                VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES
        );

        int color = xpTextProperties.getProperties().getXpText();
        return color != 0
                ? color
                : original;
    }
}
