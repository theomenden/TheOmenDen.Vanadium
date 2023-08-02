package vanadium.mixin.model;

import net.minecraft.client.renderer.block.model.FaceBakery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vanadium.models.ModelIdContext;

@Mixin(FaceBakery.class)
public class QuadFactoryMixin {
    @ModifyArg(
            method = "bakeQuad",
            at = @At(
                    value="INVOKE",
                    target="Lnet/minecraft/client/renderer/block/model/BakedQuad;<init>([IILnet/minecraft/core/Direction;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Z)V"
            )
    )
    private int addTintToCustomColoredModel(int tintIndex) {
        return ModelIdContext.isACustomeTintForCurrentModel ? 0 : tintIndex;
    }
}
