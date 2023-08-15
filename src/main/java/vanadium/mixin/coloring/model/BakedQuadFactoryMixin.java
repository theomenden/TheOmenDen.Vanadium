package vanadium.mixin.coloring.model;

import net.minecraft.client.render.model.BakedQuadFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vanadium.customcolors.decorators.ModelIdContext;

@Mixin(BakedQuadFactory.class)
public abstract class BakedQuadFactoryMixin {

    @ModifyArg(
            method="bake",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/model/BakedQuad;<init>([IILnet/minecraft/util/math/Direction;Lnet/minecraft/client/texture/Sprite;Z)V"
            )
    )
    private int addTintToCustomColoredModel(int tintIndex) {
        return ModelIdContext.shouldTintCurrentModel
                ? 0
                : tintIndex;
    }
}
