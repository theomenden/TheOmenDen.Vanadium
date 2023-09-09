package vanadium.mixin.coloring.potions;

import net.minecraft.world.item.alchemy.PotionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(PotionUtils.class)
public abstract class PotionUtilMixin {
    @ModifyConstant(
            method = "getColor(Ljava/util/Collection;)I",
            constant = @Constant(intValue = 0x385dc6)
    )
    private static int modifyWaterColor(int waterColor) {
        var colorProperties = ObjectUtils.firstNonNull(
                VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                VanadiumColormaticResolution.COLOR_PROPERTIES
        );
        int color = colorProperties
                .getProperties()
                .getPotion(null);

        return color != 0
                ? color
                : waterColor;
    }
}
