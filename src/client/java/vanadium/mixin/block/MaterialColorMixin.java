package vanadium.mixin.block;

import net.minecraft.block.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;

@Mixin(MapColor.class)
public abstract class MaterialColorMixin {
    @Inject(
            method = "getRenderColor(Lnet/minecraft/block/MapColor$Brightness;)I",
            at = @At(
                    value="FIELD",
                    target="Lnet/minecraft/block/MapColor;color:I",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void onRenderColor(MapColor.Brightness brightness, CallbackInfoReturnable<Integer> info) {
        int color = Vanadium.COLOR_PROPERTIES.getProperties().getMap((MapColor)(Object)this);

        if(color != 0) {
            int scalarFactor = brightness.brightness / 255;

            int red = ((color >> 16) & 0xff) * scalarFactor;
            int green = ((color >> 8) & 0xff) * scalarFactor;
            int blue = (color  & 0xff) * scalarFactor;

            info.setReturnValue(0xff000000|(blue << 16) | (green << 8) | red);
        }
    }
}