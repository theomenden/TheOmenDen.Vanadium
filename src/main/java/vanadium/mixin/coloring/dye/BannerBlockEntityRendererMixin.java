package vanadium.mixin.coloring.dye;

import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.world.item.DyeColor;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(BannerRenderer.class)
public abstract class BannerBlockEntityRendererMixin {

    @Redirect(
            method = "renderPatterns(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/resources/model/Material;ZLjava/util/List;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/DyeColor;getTextureDiffuseColors()[F"
            )
    )
    private static float[] proxyGetColorComponents(DyeColor instance) {
        var colorProperties = ObjectUtils.firstNonNull(
                VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                VanadiumColormaticResolution.COLOR_PROPERTIES
        );
        float[] color = colorProperties
                .getProperties()
                .getBannerRgb(instance);

        if(color != null) {
            return color;
        }

        return instance.getTextureDiffuseColors();
    }
}
