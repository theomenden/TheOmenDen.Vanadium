package vanadium.mixin.coloring.model;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.customcolors.decorators.ModelIdContext;

import java.util.List;

@Mixin(BlockModel.class)
public abstract class JsonUnbakedModelMixin {
    @Shadow
    public abstract List<BlockElement> getElements();

    @Inject(
            method="bake(Lnet/minecraft/client/resources/model/ModelBaker;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At("HEAD"))
    private void setBismuthCustomTint(CallbackInfoReturnable<BakedModel> cir) {
        if (ModelIdContext.shouldTintCurrentModel && this
                .getElements()
                .stream()
                .flatMap(element -> element.faces
                        .values()
                        .stream())
                .anyMatch(face -> face.tintIndex >= 0)) {
            ModelIdContext.shouldTintCurrentModel = false;
        }
    }

}
