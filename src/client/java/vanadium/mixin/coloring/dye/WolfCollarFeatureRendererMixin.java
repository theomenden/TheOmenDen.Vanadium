package vanadium.mixin.coloring.dye;

import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.Vanadium;


@Mixin(WolfCollarFeatureRenderer.class)
public abstract class WolfCollarFeatureRendererMixin extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
    private WolfCollarFeatureRendererMixin() {
        super(null);
    }

    @Redirect(
            method= "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/WolfEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target= "Lnet/minecraft/util/DyeColor;getColorComponents()[F"
            )
    )
    private float[] proxyGetColorComponents(DyeColor self) {
        float[] rgb = Vanadium.COLOR_PROPERTIES
                .getProperties()
                .getCollarRgb(self);

        return rgb != null
                ? rgb
                : self.getColorComponents();
    }
}
