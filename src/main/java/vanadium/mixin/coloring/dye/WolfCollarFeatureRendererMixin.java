package vanadium.mixin.coloring.dye;

import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.DyeColor;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.utils.VanadiumColormaticResolution;


@Mixin(WolfCollarLayer.class)
public abstract class WolfCollarFeatureRendererMixin extends RenderLayer<Wolf, WolfModel<Wolf>> {
    private WolfCollarFeatureRendererMixin() {
        super(null);
    }

    @Redirect(
            method= "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/Wolf;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target= "Lnet/minecraft/world/item/DyeColor;getTextureDiffuseColors()[F"
            )
    )
    private float[] proxyGetColorComponents(DyeColor instance) {
        var colorProperties = ObjectUtils.firstNonNull(
                VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                VanadiumColormaticResolution.COLOR_PROPERTIES
        );
        float[] rgb = colorProperties
                .getProperties()
                .getCollarRgb(instance);

        return rgb != null
                ? rgb
                : instance.getTextureDiffuseColors();
    }
}
