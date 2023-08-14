package vanadium.mixin.coloring.dye;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.Vanadium;

import java.util.Map;

@Mixin(SheepEntity.class)
public abstract class SheepEntityMixin extends AnimalEntity {
    private SheepEntityMixin(){
        super(null, null);
    }

    @Redirect(
            method= "getRgbColor",
            at = @At(
                    value = "INVOKE",
                    target="Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;",
                    remap = false
            )
    )
    private static Object proxyGetRgb(Map<DyeColor, float[]> self, Object key) {
        float[] rgb = Vanadium.COLOR_PROPERTIES
                .getProperties()
                .getWoolRgb((DyeColor) key);

        return rgb != null
                ? rgb
                : self.get(key);
    }
}
