package vanadium.mixin.coloring.block;

import net.minecraft.block.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.VanadiumClient;

@Mixin(MapColor.class)
public abstract class MaterialColorMixin {

    @Inject(
            method= "getRenderColor(Lnet/minecraft/block/MapColor$Brightness;)I",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/block/MapColor;color:I" ,
                    ordinal = 0
            ),
            cancellable = true
    )
    private void onRenderColor(MapColor.Brightness brightness, CallbackInfoReturnable<Integer> cir) {
        int color = VanadiumClient.COLOR_PROPERTIES
                .getProperties()
                .getMap((MapColor) (Object) this);

        if (color != 0) {
            int scalarBrightness = brightness.brightness;
            int red = ((color >> 16) & 0xff) * (scalarBrightness / 255);
            int green = ((color >> 8) & 0xff) * (scalarBrightness / 255);
            int blue = (color & 0xff) * (scalarBrightness / 255);
            cir.setReturnValue(0xff000000 | (blue << 16) | (green << 8) | red);
        }

    }
}
