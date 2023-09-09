package vanadium.mixin.coloring.dye;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.utils.VanadiumColormaticResolution;

import java.util.Map;

@Mixin(Sheep.class)
public abstract class SheepEntityMixin extends Animal {
    private SheepEntityMixin(){
        super(null, null);
    }

    @Redirect(
            method= "getColorArray",
            at = @At(
                    value = "INVOKE",
                    target="Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;",
                    remap = false
            )
    )
    private static Object proxyGetRgb(Map<DyeColor, float[]> self, Object key) {
        var colorProperties = ObjectUtils.firstNonNull(
                VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                VanadiumColormaticResolution.COLOR_PROPERTIES
        );
        float[] rgb = colorProperties
                .getProperties()
                .getWoolRgb((DyeColor) key);

        return rgb != null
                ? rgb
                : self.get(key);
    }
}
