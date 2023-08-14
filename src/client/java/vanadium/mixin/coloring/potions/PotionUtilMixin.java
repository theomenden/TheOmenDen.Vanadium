package vanadium.mixin.coloring.potions;

import net.minecraft.potion.PotionUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import vanadium.Vanadium;

@Mixin(PotionUtil.class)
public abstract class PotionUtilMixin {
    @ModifyConstant(
            method = "getColor(Ljava/util/Collection;)I",
            constant = @Constant(intValue = 0x385dc6)
    )
    private static int modifyWaterColor(int waterColor) {
        int color = Vanadium.COLOR_PROPERTIES
                .getProperties()
                .getPotion(null);

        return color != 0
                ? color
                : waterColor;
    }
}
