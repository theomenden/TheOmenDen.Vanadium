package vanadium.mixin.player;

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import vanadium.entry.Vanadium;

@Mixin(Gui.class)
public class InGameHudMixin {

    @ModifyConstant(
            method = "renderExperienceBar(Lnet/minecraft/client/gui/GuiGraphics;I)V",
            constant = @Constant(intValue = 8453920)
    )
    private int getXpTextColor(int original) {
        int col = Vanadium.COLOR_PROPERTIES.getProperties().getXpText();
        return col != 0 ? col : original;
    }

}
