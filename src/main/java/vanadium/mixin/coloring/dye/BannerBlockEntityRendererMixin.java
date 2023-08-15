package vanadium.mixin.coloring.dye;

import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.Vanadium;

@Mixin(BannerBlockEntityRenderer.class)
public abstract class BannerBlockEntityRendererMixin {

    @Redirect(
            method = "renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLjava/util/List;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/DyeColor;getColorComponents()[F"
            )
    )
    private static float[] proxyGetColorComponents(DyeColor self) {
        float[] color = Vanadium.COLOR_PROPERTIES
                .getProperties()
                .getBannerRgb(self);

        if(color != null) {
            return color;
        }

        return self.getColorComponents();
    }
}
