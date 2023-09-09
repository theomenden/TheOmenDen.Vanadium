package vanadium.mixin.coloring.block;

import net.minecraft.world.level.material.MapColor;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(MapColor.class)
public abstract class MaterialColorMixin {

    @Inject(
            method= "calculateRGBColor",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/material/MapColor;NONE:Lnet/minecraft/world/level/material/MapColor;" ,
                    ordinal = 0
            ),
            cancellable = true
    )
    private void onRenderColor(MapColor.Brightness brightness, CallbackInfoReturnable<Integer> cir) {
        var colorProperties = ObjectUtils.firstNonNull(
                VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                VanadiumColormaticResolution.COLOR_PROPERTIES
        );
        int color = colorProperties
                .getProperties()
                .getMap((MapColor) (Object) this);

        if (color != 0) {
            int scalarBrightness = brightness.modifier;
            int red = ((color >> 16) & 0xff) * (scalarBrightness / 255);
            int green = ((color >> 8) & 0xff) * (scalarBrightness / 255);
            int blue = (color & 0xff) * (scalarBrightness / 255);
            cir.setReturnValue(0xff000000 | (blue << 16) | (green << 8) | red);
        }

    }
}
