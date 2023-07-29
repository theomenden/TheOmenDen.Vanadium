package vanadium.mixin.dye;

import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.Vanadium;

@Mixin(WolfCollarLayer.class)
public abstract class WolfCollarMixin extends RenderLayer<Wolf, WolfModel<Wolf>> {
    private WolfCollarMixin() {
        super(null);
    }

    @Redirect(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/Wolf;FFFFFF)V",
            at = @At( value= "INVOKE", target="Lnet/minecraft/world/item/DyeColor;getTextureDiffuseColors()[F")
    )
    private float[] colorComponentProxy(DyeColor self) {
        float[] rgb = Vanadium.COLOR_PROPERTIES.getProperties()
                                               .getCollarRgb(self);

        return rgb != null ? rgb : self.getTextureDiffuseColors();
    }
}
