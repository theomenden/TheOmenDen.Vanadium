package vanadium.mixin.client;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import vanadium.Vanadium;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @ModifyConstant(
            method="renderExperienceBar",
            constant = @Constant(intValue = 8453920)
    )
    private int getExperienceTextColor(int original) {
        int color = Vanadium.COLOR_PROPERTIES.getProperties().getXpText();
        return color != 0
                ? color
                : original;
    }
}
